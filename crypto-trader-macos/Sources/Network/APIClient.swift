import Combine
import Foundation


class APIClient {
  static let shared = APIClient()
  private var jwtToken: String?
  
  func setToken(_ token: String) {
    self.jwtToken = token
  }
  
  // http 요청 메서드
  func request(
    url: URL,
    post: Bool = false,
    param: [String: String]? = nil,
    body: Data? = nil
  ) -> AnyPublisher<(Data, HTTPURLResponse), URLError> {
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
      .map{ return ($0.data, $0.response as! HTTPURLResponse ) } // 응답에서 data만 추출
      .eraseToAnyPublisher() // AnyPublisher로 타입 지우기
  }
  
  func fetchImageData(url: URL) -> AnyPublisher<(Data?, HTTPURLResponse?), URLError> {
    let cacheDirectory = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
    let safeFileName = url.deletingPathExtension().lastPathComponent
    let cachedUrl = cacheDirectory.appendingPathComponent(safeFileName, conformingTo: .png)
    
    if
      FileManager.default.fileExists(atPath: cachedUrl.path),
      let localData = try? Data(contentsOf: cachedUrl) {
      return Just((localData, nil))
        .setFailureType(to: URLError.self)
        .eraseToAnyPublisher()
    }
    
    let request = URLRequest(url: url)
    
    return URLSession.shared.dataTaskPublisher(for: request)
      .map { output -> (Data, HTTPURLResponse) in
        try? output.data.write(to: cachedUrl)
        return (output.data, output.response as! HTTPURLResponse)
      }
      .eraseToAnyPublisher()
  }
}
