import Foundation

enum APIEndpoint {
  static let baseURL = "http://localhost:8090"
  
  case userInfo
  case orderCreate
  case login
  case tickers
  case allCandles
  case candlesInfo
  case candles
    
  var url: URL? {
    switch self {
    case .userInfo:
      return URL(string: url("/api/users/info"))
    case .orderCreate:
      return URL(string: url("/api/orders/create"))
    case .login:
      return URL(string:  url("/login"))
    case .tickers:
      return URL(string: url("/api/tickers"))
    case .allCandles:
      return URL(string: url("/api/all-candles"))
    case .candlesInfo:
      return URL(string: url("/api/candles-info"))
    case .candles:
      return URL(string: url("/api/candles"))
    }
  }
  
  private func url(_ string: String) -> String{
    "\(APIEndpoint.baseURL)\(string)"
  }
}
