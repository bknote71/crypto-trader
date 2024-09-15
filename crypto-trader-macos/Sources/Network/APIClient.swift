import Foundation
import Combine

class APIClient {
  static let shared = APIClient()
  private var jwtToken: String?
  
  func setToken(_ token: String) {
    self.jwtToken = token
  }
  
  // POST 요청 메서드
  func request(
    url: URL,
    post: Bool = false,
    param: [String: String]? = nil,
    body: Data? = nil
  ) -> AnyPublisher<Data, URLError> {
    var request = URLRequest(url: url)
    
    if post {
      request.httpMethod = "POST"
    }
    
    // Authorization 헤더 추가
    if let token = jwtToken {
      request.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
    }
    
    // 파라미터가 있을 경우 URL에 쿼리로 추가
    if let param = param {
      var urlComponents = URLComponents(url: url, resolvingAgainstBaseURL: false)
      urlComponents?.queryItems = param.map { URLQueryItem(name: $0.key, value: $0.value) }
      if let modifiedURL = urlComponents?.url {
        request.url = modifiedURL
      }
    }
    
    // 바디 데이터 추가
    if let body = body {
      request.httpBody = body
      request.addValue("application/json", forHTTPHeaderField: "Content-Type") // JSON 데이터를 보낼 경우
    }
    
    // URLSession의 DataTaskPublisher 사용
    return URLSession.shared.dataTaskPublisher(for: request)
      .map(\.data) // 응답에서 data만 추출
      .eraseToAnyPublisher() // AnyPublisher로 타입 지우기
  }
}
