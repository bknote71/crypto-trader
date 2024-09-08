import Foundation

class WebSocketManager {
  private var webSocketTask: URLSessionWebSocketTask?
  private let urlSession = URLSession(configuration: .default, delegate: nil, delegateQueue: OperationQueue())

  func connect(url: String, callback: @escaping (Result<URLSessionWebSocketTask.Message, any Error>) throws -> Void) {
    guard let url = URL(string: url) else { return }
    print(url)
    webSocketTask = urlSession.webSocketTask(with: url)
    webSocketTask?.resume()
    receiveMessage(callback: callback)
  }

  func disconnect() {
    webSocketTask?.cancel(with: .goingAway, reason: nil)
  }

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

  private func receiveMessage(callback:  @escaping (Result<URLSessionWebSocketTask.Message, any Error>) throws -> Void) {
    webSocketTask?.receive { [weak self] result in
      try? callback(result)
      self?.receiveMessage(callback: callback)
    }
  }
}
