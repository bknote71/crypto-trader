import SwiftUI

enum OrderSide {
  case bid
  case ask
  case other
  
  var title: String {
    switch self {
    case .bid:
        "매수"
    case .ask:
        "매도"
    default:
     ""
    }
  }
  
  var color: Color {
    switch self {
    case .bid:
        .red
    case .ask:
        .blue
    default:
        .black
    }
  }
}
