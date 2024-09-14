import Foundation

extension Double {
    /// 소수점이 있을 경우 최대 3자리, 없을 경우 소수점 없이 문자열로 변환
  func formattedPrice(min: Int = 0, max: Int = 3) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = min
        formatter.maximumFractionDigits = max
        return formatter.string(from: NSNumber(value: self)) ?? ""
    }
}
