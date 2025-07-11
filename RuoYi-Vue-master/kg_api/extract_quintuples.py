import fitz  # PyMuPDF
import json
import os
import logging
from datetime import datetime
from neo4j import GraphDatabase
from tqdm import tqdm
import subprocess
import re
import requests

# === 配置 ===
NEO4J_URI = 'bolt://localhost:7687'
NEO4J_USER = 'neo4j'
NEO4J_PASSWORD = 'Satory1289'
OLLAMA_MODEL = 'qwen2.5:latest'
MAX_LEN = 500
SILICONFLOW_URL = "https://api.siliconflow.cn/v1/chat/completions"
SILICONFLOW_MODEL = "Qwen/QwQ-32B"
SILICONFLOW_API_KEY = "sk-kliintzjxqkxwjxdjvbuacpnshemgdnjtmqjcbxumkxrjvnz"

PROMPT_TEMPLATE = '''
你是一名知识图谱构建专家，专门从教材PDF中抽取结构化五元组。请严格按照以下要求操作：

1. 只处理文本内容，忽略图片、表格、公式等非文本信息。
2. 从给定文本中抽取所有 (subject, subject_type, predicate, object, object_type) 五元组。
3. 输出格式必须是**纯 JSON 数组**，不要包含任何解释、markdown标记或多余内容。
4. 如果没有可抽取的五元组，输出空数组 []。

实体类型建议：
数学领域
--数学对象
Equation(方程)、Function(函数)、Theorem(定理)、Lemma(引理)、Conjecture(猜想)、Proof(证明)、Matrix(矩阵)、Tensor(张量)、Series(级数)、Manifold(流形)
--数学概念
Concept(概念)、Property(性质)、Method(方法)、Axiom(公理)、Corollary(推论)、Paradox(悖论)、Hypothesis(假设)、Interpretation(解释)
--操作关系
derive(推导)、solve(求解)、apply(应用)、prove(证明)、approximate(逼近)、transform(变换)、classify(分类)、generalize(推广)
物理领域
--物理对象
Law(定律)、Principle(原理)、Phenomenon(现象)、Particle(粒子)、Field(场)、Constant(常量)、Dimension(量纲)、Symmetry(对称性)、QuantumState(量子态)
--物理概念
Model(模型)、Approximation(近似)、Measurement(测量)、Uncertainty(不确定性)、Duality(对偶性)、Coherence(相干性)、Decoherence(退相干)
--操作关系
observe(观测)、calculate(计算)、quantize(量子化)、normalize(归一化)、entangle(纠缠)、collapse(坍缩)、interfere(干涉)
化学领域
--化学对象
Element(元素)、Compound(化合物)、Reaction(反应)、Bond(化学键)、Orbital(轨道)、Isotope(同位素)、Polymer(聚合物)、Catalyst(催化剂)
--化学概念
Mechanism(机理)、Equilibrium(平衡)、Kinetics(动力学)、Thermodynamics(热力学)、Configuration(构型)、Conformation(构象)、Aromaticity(芳香性)
--操作关系
oxidize(氧化)、reduce(还原)、dissociate(解离)、polymerize(聚合)、catalyze(催化)、titrate(滴定)、distill(蒸馏)
生物领域
--生物对象
Gene(基因)、Protein(蛋白质)、Cell(细胞)、Organism(生物体)、Enzyme(酶)、Pathway(通路)、Ecosystem(生态系统)、Taxon(分类单元)
--生物概念
Evolution(进化)、Mutation(突变)、Expression(表达)、Regulation(调控)、Homeostasis(稳态)、Adaptation(适应)、Speciation(物种形成)
--操作关系
transcribe(转录)、translate(翻译)、replicate(复制)、mutate(突变)、inhibit(抑制)、activate(激活)、migrate(迁移)

请处理以下文本：
{{chunk_text}}

请严格只输出如下格式：
[
  {
    "subject": "...",
    "subject_type": "...",
    "predicate": "...",
    "object": "...",
    "object_type": "..."
  }
]
'''


def setup_logging(log_dir: str) -> str:
    os.makedirs(log_dir, exist_ok=True)
    log_file = os.path.join(log_dir, f"extract_{datetime.now():%Y%m%d_%H%M%S}.log")
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s [%(levelname)s] %(message)s',
        handlers=[
            logging.FileHandler(log_file, encoding='utf-8'),
            logging.StreamHandler()
        ]
    )
    return log_file

def extract_text(pdf_path: str) -> str:
    doc = fitz.open(pdf_path)
    return "\n".join(page.get_text() for page in doc)

def sanitize_text(text: str) -> str:
    return text.replace('\u2011', '-')

def chunk_text(text: str, max_len: int = MAX_LEN) -> list:
    paras = text.split('\n\n')
    chunks, buf = [], ''
    for p in paras:
        p_s = sanitize_text(p)
        if len(buf) + len(p_s) < max_len:
            buf += p_s + '\n\n'
        else:
            if buf.strip():
                chunks.append(buf.strip())
            buf = p_s + '\n\n'
    if buf.strip():
        chunks.append(buf.strip())
    return chunks

def call_ollama(prompt: str, model: str = None) -> str:
    payload = {
        "model": SILICONFLOW_MODEL,
        "messages": [
            {
                "role": "user",
                "content": prompt
            }
        ]
    }
    headers = {
        "Authorization": f"Bearer {SILICONFLOW_API_KEY}",
        "Content-Type": "application/json"
    }
    try:
        response = requests.post(SILICONFLOW_URL, json=payload, headers=headers, timeout=300)
        response.raise_for_status()
        data = response.json()
        return data["choices"][0]["message"]["content"]
    except Exception as e:
        logging.error(f"硅基流动API调用失败: {e}")
        return ""

def parse_quintuples(llm_output: str) -> list:
    llm_output = llm_output.replace("```json", "")
    llm_output = llm_output.replace("```", "")
    data = json.loads(llm_output)
    return [q for q in data if all(k in q for k in ('subject', 'subject_type', 'predicate', 'object', 'object_type'))]

def safe_labels(label_str):
    # 支持多 label，点号分割
    return ':'.join([l for l in label_str.replace(' ', '_').split('.') if l])

def upload_quintuples(quintuples: list, uri: str, user: str, password: str, course_id: str, file_name: str):
    logging.info(f"准备写入Neo4j: {len(quintuples)}个五元组, courseId={course_id}, fileName={file_name}")
    driver = GraphDatabase.driver(uri, auth=(user, password))
    with driver.session() as session:
        for q in quintuples:
            s, stype, p, o, otype = q['subject'], q['subject_type'], q['predicate'], q['object'], q['object_type']
            stype_labels = safe_labels(stype)
            otype_labels = safe_labels(otype)
            cypher = (
                f"MERGE (s:{stype_labels} {{name: $subj, courseId: $courseId, fileName: $fileName}}) "
                f"MERGE (o:{otype_labels} {{name: $obj, courseId: $courseId, fileName: $fileName}}) "
                f"MERGE (s)-[r:`{p}`]->(o)"
            )
            session.run(cypher, subj=s, obj=o, courseId=course_id, fileName=file_name)
    driver.close()
    logging.info("Neo4j写入完成")

def clean_text(text: str) -> str:
    # 去除"图1"、"见下图"等常见图片描述
    text = re.sub(r'图\s*\d+[^。；\n]*[。；\n]', '', text)
    text = re.sub(r'见下图[^。；\n]*[。；\n]', '', text)
    # 其他自定义清洗规则
    return text

def extract_and_upload(pdf_paths: list, course_id: str, neo4j_conf: dict, out_dir: str = None):
    """
    兼容单课程多PDF的处理
    """
    results = []
    for pdf_path in pdf_paths:
        file_name = os.path.basename(pdf_path)
        logging.info(f"处理PDF: {pdf_path}")
        try:
            raw = sanitize_text(extract_text(pdf_path))
            raw = clean_text(raw)
            chunks = chunk_text(raw)
            all_quintuples = []
            for idx, chunk in enumerate(tqdm(chunks, desc=f'Processing {file_name}'), 1):
                if not chunk.strip():
                    continue
                chunk = chunk.replace('\n', ' ').replace('\t', ' ').strip()
                chunk = ' '.join(chunk.split())
                prompt = PROMPT_TEMPLATE.replace("{{chunk_text}}", chunk)
                try:
                    out = call_ollama(prompt, OLLAMA_MODEL)
                    out = out.replace("```json", "").replace("```", "")
                    if not out.strip():
                        continue
                    quintuples = parse_quintuples(out)
                    all_quintuples.extend(quintuples)
                except Exception as e:
                    logging.error(f"Failed on chunk {idx}: {e}")
            # 上传到Neo4j
            upload_quintuples(all_quintuples, neo4j_conf['uri'], neo4j_conf['user'], neo4j_conf['password'], course_id, file_name)
            results.append({
                "pdf": file_name,
                "courseId": course_id,
                "quintuples": all_quintuples
            })
        except Exception as e:
            logging.error(f"处理PDF失败: {pdf_path}, 错误: {e}")
            results.append({
                "pdf": file_name,
                "courseId": course_id,
                "quintuples": [],
                "error": str(e)
            })
    return results

def extract_and_upload_by_course(course_files: list, neo4j_conf: dict, out_dir: str = None):
    """
    course_files: [
        {"courseId": "1", "pdf_paths": ["/path/to/a.pdf", ...]},
        ...
    ]
    """
    all_results = []
    for course in course_files:
        course_id = course['courseId']
        pdf_paths = course['pdf_paths']
        for pdf_path in pdf_paths:
            file_name = os.path.basename(pdf_path)
            logging.info(f"处理课程{course_id}的PDF: {pdf_path}")
            try:
                raw = sanitize_text(extract_text(pdf_path))
                raw = clean_text(raw)
                chunks = chunk_text(raw)
                all_quintuples = []
                for idx, chunk in enumerate(tqdm(chunks, desc=f'Processing {file_name}'), 1):
                    if not chunk.strip():
                        continue
                    chunk = chunk.replace('\n', ' ').replace('\t', ' ').strip()
                    chunk = ' '.join(chunk.split())
                    prompt = PROMPT_TEMPLATE.replace("{{chunk_text}}", chunk)
                    try:
                        out = call_ollama(prompt, OLLAMA_MODEL)
                        out = out.replace("```json", "").replace("```", "")
                        if not out.strip():
                            continue
                        quintuples = parse_quintuples(out)
                        all_quintuples.extend(quintuples)
                    except Exception as e:
                        logging.error(f"Failed on chunk {idx}: {e}")
                # 上传到Neo4j
                upload_quintuples(all_quintuples, neo4j_conf['uri'], neo4j_conf['user'], neo4j_conf['password'], course_id, file_name)
                all_results.append({
                    "courseId": course_id,
                    "pdf": file_name,
                    "quintuples": all_quintuples
                })
            except Exception as e:
                logging.error(f"处理PDF失败: {pdf_path}, 错误: {e}")
                all_results.append({
                    "courseId": course_id,
                    "pdf": file_name,
                    "quintuples": [],
                    "error": str(e)
                })
    return all_results 