import SwiftUI

extension Color {
  // Define gray colors
  static let gray50 = Color(r: 242, g: 242, b: 242)
  static let gray100 = Color(r: 230, g: 230, b: 230)
  static let gray150 = Color(r: 217, g: 217, b: 217)
  static let gray200 = Color(r: 204, g: 204, b: 204)
  static let gray250 = Color(r: 191, g: 191, b: 191)
  static let gray300 = Color(r: 179, g: 179, b: 179)
  static let gray350 = Color(r: 166, g: 166, b: 166)
  static let gray400 = Color(r: 153, g: 153, b: 153)
  static let gray450 = Color(r: 140, g: 140, b: 140)
  static let gray500 = Color(r: 128, g: 128, b: 128)
  static let gray550 = Color(r: 115, g: 115, b: 115)
  static let gray600 = Color(r: 102, g: 102, b: 102)
  static let gray650 = Color(r: 89, g: 89, b: 89)
  static let gray700 = Color(r: 77, g: 77, b: 77)
  static let gray750 = Color(r: 64, g: 64, b: 64)
  static let gray800 = Color(r: 51, g: 51, b: 51)
  static let gray850 = Color(r: 38, g: 38, b: 38)
  static let gray900 = Color(r: 26, g: 26, b: 26)
  static let gray950 = Color(r: 13, g: 13, b: 13)
  
  // Initialize Color with RGB values
  init(r: Int, g: Int, b: Int) {
    self.init(
      .sRGB,
      red: Double(r) / 255.0,
      green: Double(g) / 255.0,
      blue: Double(b) / 255.0,
      opacity: 1.0
    )
  }
}

