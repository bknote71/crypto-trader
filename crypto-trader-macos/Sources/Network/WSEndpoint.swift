import Foundation

enum WSEndpoint {
  static let baseUrl = "ws://localhost:8090"
  
  case ticker
  case candle
  
  var url: URL? {
    switch self {
    case .ticker:
      return URL(string: "\(Self.baseUrl)/ticker")
    case .candle:
      return URL(string: "\(Self.baseUrl)/candle")
    }
  }
}
