import Combine
import Foundation

// 제네릭 프로토콜: associatedtype 사용
protocol WSClient {
  associatedtype Content
  associatedtype Failure: Error
  
  var publisher: PassthroughSubject<Content, Failure> { get }
  var webSocketTask: URLSessionWebSocketTask? { get }
  var urlSession: URLSession { get }
  
  func connect(url: URL) -> PassthroughSubject<Content, Failure>
  func disconnect()
  func sendMessage(_ message: String)
  func receiveMessage()
}

extension WSClient {
  func sendMessage(_ message: String) {
    let message = URLSessionWebSocketTask.Message.string(message)
    webSocketTask?.send(message) { error in
      if let error = error {
        print("메시지 전송 오류: \(error)")
      } else {
        print("메시지 전송 성공")
      }
    }
  }
}

class JsonWSClient<V: Decodable>: WSClient {
  
  private(set) var publisher = PassthroughSubject<V, Never>()
  private(set) var webSocketTask: URLSessionWebSocketTask?
  let urlSession = URLSession(configuration: .default, delegate: nil, delegateQueue: OperationQueue())
  
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
  
  var isConnected: Bool {
    webSocketTask != nil
  }
  
  func connect(url: URL) ->  PassthroughSubject<V, Never> {
    let webSocketTask = urlSession.webSocketTask(with: url)
    webSocketTask.resume()
    self.webSocketTask = webSocketTask
    
    receiveMessage()
    
    return publisher
  }
  
  func disconnect() {
    webSocketTask?.cancel(with: .goingAway, reason: nil)
    webSocketTask = nil
  }
  
  internal func receiveMessage() {
    webSocketTask?.receive { [weak self] result in
      switch result {
      case .failure(let error):
        print("메시지 수신 오류: \(error)")
      case .success(let message):
        switch message {
        case .string(let text):
          self?.processJson(text)
        case .data(_):
          assertionFailure()
        @unknown default:
          print("알 수 없는 메시지 수신")
        }
        // 다음 메시지를 계속 수신하도록 호출
        self?.receiveMessage()
      }
    }
  }
  
  private func processJson(_ text: String) {
    guard let data = text.data(using: .utf8) else {
      // throw
      return
    }
    
    do {
      let decodedMessage = try decoder.decode(V.self, from: data)
      publisher.send(decodedMessage)
    } catch {
      print("디코딩 오류: \(error)")
    }
  }
}
