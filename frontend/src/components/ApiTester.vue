<template>
  <div class="api-tester">
    <h2>API 联调测试</h2>
    <button @click="runTests" :disabled="testing" class="test-button">
      {{ testing ? '测试中...' : '运行API测试' }}
    </button>

    <div v-if="results.length > 0" class="results">
      <div class="summary">
        <h3>测试结果摘要</h3>
        <p>总计: {{ summary.total }} | 成功: {{ summary.success }} | 失败: {{ summary.error }}</p>
        <p>成功率: {{ summary.successRate }}%</p>
      </div>

      <div class="test-results">
        <h3>详细结果</h3>
        <div v-for="(result, index) in results" :key="index" 
             :class="['result-item', result.status]">
          <div class="result-header">
            <span class="method">{{ result.method }}</span>
            <span class="endpoint">{{ result.endpoint }}</span>
            <span :class="['status', result.status]">{{ result.status }}</span>
          </div>
          <div class="result-message">{{ result.message }}</div>
          <div v-if="result.response" class="response-data">
            <details>
              <summary>响应数据</summary>
              <pre>{{ JSON.stringify(result.response, null, 2) }}</pre>
            </details>
          </div>
          <div v-if="result.error" class="error-data">
            <details>
              <summary>错误信息</summary>
              <pre>{{ result.error }}</pre>
            </details>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ApiTester } from '../utils/apiTester';

const testing = ref(false);
const results = ref([]);
const summary = ref({ total: 0, success: 0, error: 0, successRate: 0 });

const runTests = async () => {
  testing.value = true;
  results.value = [];
  
  try {
    const tester = new ApiTester();
    const testResults = await tester.testAllEndpoints();
    results.value = testResults;
    summary.value = tester.getSummary();
  } catch (error) {
    console.error('测试运行失败:', error);
    results.value = [{
      endpoint: '测试运行',
      method: 'TEST',
      status: 'error',
      message: '测试运行失败',
      error: error.message
    }];
  } finally {
    testing.value = false;
  }
};
</script>

<style scoped>
.api-tester {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-bottom: 20px;
}

.test-button:hover:not(:disabled) {
  background-color: #0056b3;
}

.test-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.results {
  margin-top: 20px;
}

.summary {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.summary h3 {
  margin-top: 0;
}

.test-results {
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.test-results h3 {
  padding: 15px;
  margin: 0;
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.result-item {
  padding: 15px;
  border-bottom: 1px solid #dee2e6;
}

.result-item:last-child {
  border-bottom: none;
}

.result-item.success {
  background-color: #d4edda;
}

.result-item.error {
  background-color: #f8d7da;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.method {
  font-weight: bold;
  color: #495057;
}

.endpoint {
  flex: 1;
  margin-left: 10px;
  color: #6c757d;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status.success {
  background-color: #28a745;
  color: white;
}

.status.error {
  background-color: #dc3545;
  color: white;
}

.result-message {
  margin-bottom: 10px;
  color: #495057;
}

.response-data, .error-data {
  margin-top: 10px;
}

.response-data details, .error-data details {
  background-color: white;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.response-data pre, .error-data pre {
  margin: 0;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}

summary {
  cursor: pointer;
  font-weight: bold;
  color: #007bff;
}
</style>