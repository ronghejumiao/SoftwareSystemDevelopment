-- 修改score表，添加试卷详情字段
ALTER TABLE score ADD COLUMN answer_details JSON COMMENT '试卷详情，存储学生作答情况';

-- 更新现有记录的answer_details字段为空
UPDATE score SET answer_details = NULL WHERE answer_details IS NULL; 