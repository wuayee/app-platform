delete
from store_model;

insert
into store_model ("task_name", "name", "url", "context")
values ('fill-mask', 'google-bert/bert-base-uncased', 'huggingface.co/google-bert/bert-base-uncased',
        '{"likes": 1600,"downloads": 65937583,"description": "Pretrained model on English language using a masked language modeling (MLM) objective. It was introduced in this paper and first released in this repository. This model is uncased: it does not make a difference between english and English."}'),
       ('fill-mask', 'google-bert/bert-base-chinese', 'huggingface.co/google-bert/bert-base-chinese',
        '{"likes": 853,"downloads": 2595358,"description": "This model has been pre-trained for Chinese, training and random input masking has been applied independently to word pieces (as in the original BERT paper)."}'),
       ('fill-mask', 'FacebookAI/xlm-roberta-base', 'huggingface.co/FacebookAI/xlm-roberta-base',
        '{"likes": 518,"downloads": 6329031,"description": "XLM-RoBERTa model pre-trained on 2.5TB of filtered CommonCrawl data containing 100 languages. It was introduced in the paper Unsupervised Cross-lingual Representation Learning at Scale by Conneau et al. and first released in this repository."}');