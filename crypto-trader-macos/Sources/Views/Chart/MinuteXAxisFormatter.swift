import DGCharts
import Foundation

// x축 포메터
class MinuteXAxisFormatter: NSObject, AxisValueFormatter {
  var startDate: Date?
  let dateFormatter: DateFormatter
  
  init(startDate: Date = Date.now) {
    self.startDate = startDate
    self.dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "MM/dd HH:mm" // 날짜와 시간을 원하는 형식으로 설정
  }
  
  func stringForValue(_ value: Double, axis: AxisBase?) -> String {
    guard let startDate = startDate else {
      return ""
    }
    // 시작 날짜 + x값을 1분 단위로 더함
    let date = Calendar.current.date(byAdding: .minute, value: Int(value), to: startDate)!
    return dateFormatter.string(from: date)
  }
}
