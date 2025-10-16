import axios from './axios';

export interface ApiTestResult {
  endpoint: string;
  method: string;
  status: 'success' | 'error';
  message: string;
  response?: any;
  error?: any;
}

export class ApiTester {
  private results: ApiTestResult[] = [];

  async testAllEndpoints(): Promise<ApiTestResult[]> {
    this.results = [];

    // 测试认证相关API
    await this.testAuthEndpoints();
    
    // 测试相册相关API
    await this.testAlbumEndpoints();
    
    // 测试媒体相关API
    await this.testMediaEndpoints();
    
    // 测试评论相关API
    await this.testCommentEndpoints();

    return this.results;
  }

  private async testAuthEndpoints() {
    // 测试获取当前用户
    try {
      const response = await axios.get('/api/v1/auth/me');
      this.addResult('GET', '/api/v1/auth/me', 'success', '用户已登录', response.data);
    } catch (error: any) {
      this.addResult('GET', '/api/v1/auth/me', 'error', '用户未登录', undefined, error.message);
    }
  }

  private async testAlbumEndpoints() {
    // 测试获取公开相册
    try {
      const response = await axios.get('/api/v1/albums/public');
      this.addResult('GET', '/api/v1/albums/public', 'success', '公开相册获取成功', response.data);
    } catch (error: any) {
      this.addResult('GET', '/api/v1/albums/public', 'error', '公开相册获取失败', undefined, error.message);
    }

    // 测试获取我的相册
    try {
      const response = await axios.get('/api/v1/albums/my');
      this.addResult('GET', '/api/v1/albums/my', 'success', '我的相册获取成功', response.data);
    } catch (error: any) {
      this.addResult('GET', '/api/v1/albums/my', 'error', '我的相册获取失败', undefined, error.message);
    }
  }

  private async testMediaEndpoints() {
    // 测试媒体列表格式
    try {
      // 先获取一个相册ID
      const albumsResponse = await axios.get('/api/v1/albums/public');
      if (albumsResponse.data.content && albumsResponse.data.content.length > 0) {
        const albumId = albumsResponse.data.content[0].id;
        
        // 测试获取相册媒体
        try {
          const mediaResponse = await axios.get(`/api/v1/media/album/${albumId}`);
          this.addResult('GET', `/api/v1/media/album/${albumId}`, 'success', '相册媒体获取成功', mediaResponse.data);
        } catch (error: any) {
          this.addResult('GET', `/api/v1/media/album/${albumId}`, 'error', '相册媒体获取失败', undefined, error.message);
        }
      } else {
        this.addResult('GET', '/api/v1/media/album/{id}', 'error', '没有可用的相册进行测试', undefined, 'No albums found');
      }
    } catch (error: any) {
      this.addResult('GET', '/api/v1/media/album/{id}', 'error', '无法获取相册列表', undefined, error.message);
    }
  }

  private async testCommentEndpoints() {
    // 测试评论API格式
    try {
      // 先获取一个媒体ID
      const albumsResponse = await axios.get('/api/v1/albums/public');
      if (albumsResponse.data.content && albumsResponse.data.content.length > 0) {
        const albumId = albumsResponse.data.content[0].id;
        const mediaResponse = await axios.get(`/api/v1/media/album/${albumId}`);
        
        if (mediaResponse.data && mediaResponse.data.length > 0) {
          const mediaId = mediaResponse.data[0].id;
          
          // 测试获取媒体评论
          try {
            const commentsResponse = await axios.get(`/api/v1/media/${mediaId}/comments`);
            this.addResult('GET', `/api/v1/media/${mediaId}/comments`, 'success', '媒体评论获取成功', commentsResponse.data);
          } catch (error: any) {
            this.addResult('GET', `/api/v1/media/${mediaId}/comments`, 'error', '媒体评论获取失败', undefined, error.message);
          }
        } else {
          this.addResult('GET', '/api/v1/media/{id}/comments', 'error', '没有可用的媒体进行测试', undefined, 'No media found');
        }
      } else {
        this.addResult('GET', '/api/v1/media/{id}/comments', 'error', '没有可用的相册进行测试', undefined, 'No albums found');
      }
    } catch (error: any) {
      this.addResult('GET', '/api/v1/media/{id}/comments', 'error', '无法获取测试数据', undefined, error.message);
    }
  }

  private addResult(method: string, endpoint: string, status: 'success' | 'error', message: string, response?: any, error?: any) {
    this.results.push({
      endpoint,
      method,
      status,
      message,
      response,
      error
    });
  }

  getResults() {
    return this.results;
  }

  getSummary() {
    const total = this.results.length;
    const success = this.results.filter(r => r.status === 'success').length;
    const error = total - success;

    return {
      total,
      success,
      error,
      successRate: total > 0 ? Math.round((success / total) * 100) : 0
    };
  }
}