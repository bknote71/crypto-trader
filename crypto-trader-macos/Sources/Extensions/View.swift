import SwiftUI

extension View {
  
  @ViewBuilder
  func `if`(_ condition: Bool, _ transform: (Self) -> some View) -> some View {
    if condition {
      transform(self)
    } else {
      self
    }
  }
}
