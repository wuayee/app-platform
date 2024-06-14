import { Drawer } from 'antd';
import React, { useEffect } from 'react';
import { useParams } from 'react-router';
import { Input } from 'antd';

const { TextArea } = Input;

interface props {
  visible: boolean,
  callback: Function
}

const ModelConfig = ({ visible, callback }: props) => {

  const { id } = useParams();

  const configData = [
    `{
      "architectures": [
        "Qwen2ForCausalLM"
      ],
      "attention_dropout": 0.0,
      "bos_token_id": 151643,
      "eos_token_id": 151645,
      "hidden_act": "silu",
      "hidden_size": 3584,
      "initializer_range": 0.02,
      "intermediate_size": 18944,
      "max_position_embeddings": 32768,
      "max_window_layers": 28,
      "model_type": "qwen2",
      "num_attention_heads": 28,
      "num_hidden_layers": 28,
      "num_key_value_heads": 4,
      "rms_norm_eps": 1e-06,
      "rope_theta": 1000000.0,
      "sliding_window": 131072,
      "tie_word_embeddings": false,
      "torch_dtype": "bfloat16",
      "transformers_version": "4.41.2",
      "use_cache": true,
      "use_sliding_window": false,
      "vocab_size": 152064
    }`,
    `{
        "architectures": [
          "Qwen2ForCausalLM"
        ],
        "attention_dropout": 0.0,
        "bos_token_id": 151643,
        "eos_token_id": 151645,
        "hidden_act": "silu",
        "hidden_size": 8192,
        "initializer_range": 0.02,
        "intermediate_size": 29568,
        "max_position_embeddings": 32768,
        "max_window_layers": 80,
        "model_type": "qwen2",
        "num_attention_heads": 64,
        "num_hidden_layers": 80,
        "num_key_value_heads": 8,
        "rms_norm_eps": 1e-06,
        "rope_theta": 1000000.0,
        "sliding_window": 131072,
        "tie_word_embeddings": false,
        "torch_dtype": "bfloat16",
        "transformers_version": "4.40.1",
        "use_cache": true,
        "use_sliding_window": false,
        "vocab_size": 152064
      }`,
    `{
        "architectures": [
          "LlamaForCausalLM"
        ],
        "attention_bias": false,
        "attention_dropout": 0.0,
        "bos_token_id": 128000,
        "eos_token_id": 128009,
        "hidden_act": "silu",
        "hidden_size": 4096,
        "initializer_range": 0.02,
        "intermediate_size": 14336,
        "max_position_embeddings": 8192,
        "model_type": "llama",
        "num_attention_heads": 32,
        "num_hidden_layers": 32,
        "num_key_value_heads": 8,
        "pretraining_tp": 1,
        "rms_norm_eps": 1e-05,
        "rope_scaling": null,
        "rope_theta": 500000.0,
        "tie_word_embeddings": false,
        "torch_dtype": "bfloat16",
        "transformers_version": "4.40.0.dev0",
        "use_cache": true,
        "vocab_size": 128256
      }`,
    `{
        "_name_or_path": "THUDM/glm4-9b-chat",
        "model_type": "chatglm",
        "architectures": [
          "ChatGLMModel"
        ],
        "auto_map": {
          "AutoConfig": "configuration_chatglm.ChatGLMConfig",
          "AutoModel": "modeling_chatglm.ChatGLMForConditionalGeneration",
          "AutoModelForCausalLM": "modeling_chatglm.ChatGLMForConditionalGeneration",
          "AutoModelForSeq2SeqLM": "modeling_chatglm.ChatGLMForConditionalGeneration",
          "AutoModelForSequenceClassification": "modeling_chatglm.ChatGLMForSequenceClassification"
        },
        "add_bias_linear": false,
        "add_qkv_bias": true,
        "apply_query_key_layer_scaling": true,
        "apply_residual_connection_post_layernorm": false,
        "attention_dropout": 0.0,
        "attention_softmax_in_fp32": true,
        "bias_dropout_fusion": true,
        "ffn_hidden_size": 13696,
        "fp32_residual_connection": false,
        "hidden_dropout": 0.0,
        "hidden_size": 4096,
        "kv_channels": 128,
        "layernorm_epsilon": 1.5625e-07,
        "multi_query_attention": true,
        "multi_query_group_num": 2,
        "num_attention_heads": 32,
        "num_hidden_layers": 40,
        "num_layers": 40,
        "rope_ratio": 500,
        "original_rope": true,
        "padded_vocab_size": 151552,
        "post_layer_norm": true,
        "rmsnorm": true,
        "seq_length": 131072,
        "use_cache": true,
        "torch_dtype": "bfloat16",
        "transformers_version": "4.30.2",
        "tie_word_embeddings": false,
        "eos_token_id": [151329, 151336, 151338],
        "pad_token_id": 151329
      }`,
  ];

  return (
    <Drawer
      title='配置详情'
      open={visible}
      width={520}
      onClose={() => callback()}
      destroyOnClose={true}
    >
      <TextArea value={configData[Number(id) - 1]} rows={30} />
    </Drawer>
  )
}

export default ModelConfig;
