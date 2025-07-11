import os
import json
import time
from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
from extract_quintuples import extract_and_upload, extract_and_upload_by_course, NEO4J_URI, NEO4J_USER, NEO4J_PASSWORD
from neo4j import GraphDatabase
import threading

UPLOAD_FOLDER = './uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

PROFILE_PATH = "C:/SoftwareSystemDevelopment/ruoyi/uploadPath"

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# 简单全局锁，防止并发重复抽取
extracting_lock = threading.Lock()
extracting_flag = set()  # (courseId, tuple(pdfNames)) 作为key

# 支持多文件上传和多路径
@app.route('/api/kg/extract', methods=['POST'])
def extract_kg():
    # 只允许后端自动调用，不建议前端直接用
    print("==== /api/kg/extract 被调用 ====")
    neo4j_conf = {
        "uri": NEO4J_URI,
        "user": NEO4J_USER,
        "password": NEO4J_PASSWORD
    }
    if request.is_json:
        data = request.get_json()
    else:
        data = request.form.to_dict()
    # 批量多课程
    if 'course_files' in data:
        course_files = data['course_files']
        if isinstance(course_files, str):
            try:
                course_files = json.loads(course_files)
            except Exception:
                return jsonify({"error": "course_files参数格式错误"}), 400
        if not isinstance(course_files, list):
            return jsonify({"error": "course_files参数必须为列表"}), 400
        results = extract_and_upload_by_course(course_files, neo4j_conf)
        return jsonify(results)
    # 单课程多文件/多路径
    course_id = data.get('courseId')
    pdf_paths = []
    if 'files' in request.files:
        files = request.files.getlist('files')
        for file in files:
            filename = file.filename or 'unnamed.pdf'
            save_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            file.save(save_path)
            pdf_paths.append(save_path)
    elif 'pdf_paths' in data:
        pdf_paths = data['pdf_paths']
        if isinstance(pdf_paths, str):
            try:
                pdf_paths = json.loads(pdf_paths)
            except Exception:
                pdf_paths = [pdf_paths]
        if not isinstance(pdf_paths, list):
            pdf_paths = [pdf_paths]
        def fix_path(path):
            if path.startswith("/profile/"):
                path = path[len("/profile"):]
            return os.path.normpath(os.path.join(PROFILE_PATH, path.lstrip("/")))
        pdf_paths = [fix_path(path) for path in pdf_paths]
    else:
        return jsonify({"error": "未检测到文件或pdf_paths参数"}), 400
    results = extract_and_upload(pdf_paths, course_id, neo4j_conf)
    return jsonify(results)

# 查询知识图谱（按课程ID）
@app.route('/api/kg/graph', methods=['POST'])
def get_kg_graph():
    data = request.get_json()
    course_id = data.get('courseId')
    try:
        course_id = int(course_id)
    except Exception:
        pass  # 如果本来就是int，不变
    file_name = data.get('fileName')
    pdf_names = data.get('pdfNames', [])

    if not course_id or not pdf_names:
        return jsonify({"error": "缺少courseId或pdfNames参数"}), 400

    def query_neo4j(course_id, file_name=None):
        driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
        with driver.session() as session:
            nodes, rels, node_ids = [], [], set()
            if file_name:
                result = session.run(
                    "MATCH (a)-[r]->(b) WHERE a.courseId=$courseId AND a.fileName=$fileName AND b.courseId=$courseId AND b.fileName=$fileName RETURN a, r, b",
                    courseId=course_id, fileName=file_name)
            else:
                result = session.run(
                    "MATCH (a)-[r]->(b) WHERE a.courseId=$courseId AND b.courseId=$courseId RETURN a, r, b",
                    courseId=course_id)
            for record in result:
                a = dict(record['a'])
                b = dict(record['b'])
                r = record['r'].type
                for node in [a, b]:
                    node_key = (node['name'], node.get('fileName', ''), node.get('courseId', ''))
                    if node_key not in node_ids:
                        nodes.append(node)
                        node_ids.add(node_key)
                rels.append({
                    "source": a['name'],
                    "target": b['name'],
                    "type": r,
                    "fileName": a.get('fileName', '')
                })
        driver.close()
        return nodes, rels

    # 1. 先查Neo4j
    print(f"查询Neo4j: courseId={course_id}({type(course_id)}), fileName={file_name}")
    nodes, rels = query_neo4j(course_id, file_name)
    print(f"Neo4j查到节点数: {len(nodes)}, 关系数: {len(rels)}")
    if nodes:
        return jsonify({"nodes": nodes, "edges": rels})

    # 2. 没有数据，判断哪些pdf缺失，自动触发抽取（加锁防并发）
    key = (str(course_id), tuple(pdf_names))
    if key in extracting_flag:
        return jsonify({"status": "initializing", "msg": "知识图谱正在初始化，请稍后刷新"}), 202
    with extracting_lock:
        if key in extracting_flag:
            return jsonify({"status": "initializing", "msg": "知识图谱正在初始化，请稍后刷新"}), 202
        extracting_flag.add(key)
    try:
        missing_pdfs = []
        for fname in pdf_names:
            n, _ = query_neo4j(course_id, fname)
            if not n:
                missing_pdfs.append(fname)
        if missing_pdfs:
            def fix_path(path):
                if path.startswith("/profile/"):
                    path = path[len("/profile"):]
                return os.path.normpath(os.path.join(PROFILE_PATH, path.lstrip("/")))
            pdf_paths = [fix_path(fname) for fname in missing_pdfs]
            extract_and_upload(pdf_paths, course_id, {
                "uri": NEO4J_URI,
                "user": NEO4J_USER,
                "password": NEO4J_PASSWORD
            })
    finally:
        extracting_flag.discard(key)

    # 3. 轮询等待Neo4j数据
    max_wait = 30
    interval = 2
    waited = 0
    while waited < max_wait:
        nodes, rels = query_neo4j(course_id, file_name)
        if nodes:
            return jsonify({"nodes": nodes, "edges": rels})
        time.sleep(interval)
        waited += interval

    return jsonify({"status": "initializing", "msg": "知识图谱正在初始化，请稍后刷新"}), 202

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5005, debug=True) 