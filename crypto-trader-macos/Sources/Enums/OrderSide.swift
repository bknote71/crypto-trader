import SwiftUI

enum OrderSide: String {
  case BID
  case ASK
  case other
  
  var title: String {
    switch self {
    case .BID:
        "매수"
    case .ASK:
        "매도"
    default:
     ""
    }
  }
  
  var color: Color {
    switch self {
    case .BID:
        .red
    case .ASK:
        .blue
    default:
        .black
    }
  }
}
