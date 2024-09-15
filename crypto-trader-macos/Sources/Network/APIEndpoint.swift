import Foundation

enum APIEndpoint {
  static let baseURL = "http://localhost:8090"
  
  case userInfo
  case orderCreate
  case login
    
  var url: URL? {
    switch self {
    case .userInfo:
      return URL(string: url("/api/users/info"))
    case .orderCreate:
      return URL(string: url("/api/orders/create"))
    case .login:
      return URL(string:  url("/login"))
    }
  }
  
  private func url(_ string: String) -> String{
    "\(APIEndpoint.baseURL)\(string)"
  }
}
