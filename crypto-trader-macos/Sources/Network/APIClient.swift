import Combine
import Foundation


class APIClient {
  static let shared = APIClient()
  private var jwtToken: String?
  
  func setToken(_ token: String) {
    self.jwtToken = token
  }
  
  func addToken(_ request: inout URLRequest) {
    if let token = jwtToken {
      request.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
    }
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
    addToken(&request)
    
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

class JsonAPIClient: APIClient {
  private let decoder: JSONDecoder = {
    let decoder = JSONDecoder()
    decoder.dateDecodingStrategy = .custom { decoder in
      let container = try decoder.singleValueContainer()
      let dateString = try container.decode(String.self)
      
      // 지원하는 날짜 형식 리스트
      let dateFormats = [
        "yyyy-MM-dd'T'HH:mm:ss.SSSSS", // 밀리초/마이크로초 포함
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ssXXXXX"
      ]
      
      for format in dateFormats {
        let formatter = DateFormatter()
        formatter.dateFormat = format
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        
        if let date = formatter.date(from: dateString) {
          return date
        }
      }
      
      // 날짜가 형식에 맞지 않으면 nil 반환
      throw DecodingError.dataCorruptedError(in: container, debugDescription: "Date string does not match expected format.")
    }
    return decoder
  }()
  
  func request<V: Decodable>(
    url: URL,
    post: Bool = false,
    param: [String: String]? = nil,
    body: Data? = nil
  ) -> AnyPublisher<(V?, HTTPURLResponse), URLError> {
    var request = URLRequest(url: url)
    
    if post {
      request.httpMethod = "POST"
    }
    
    // Authorization 헤더 추가
    addToken(&request)
    
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
      .map{ [weak self] in
        let result = try? self?.decoder.decode(V.self, from: $0.data)
        return (result, $0.response as! HTTPURLResponse )
      } // 응답에서 data만 추출
      .eraseToAnyPublisher() // AnyPublisher로 타입 지우기
  }
}

