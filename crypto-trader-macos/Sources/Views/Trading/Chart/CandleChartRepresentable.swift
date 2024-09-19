import Charts
import DGCharts
import SwiftUI

struct CandleChartRepresentable: NSViewRepresentable {
  @Binding var entries: [CandleChartDataEntry]
  @Binding var crypto: Crypto?
  @Binding var visibleCount: Double
  @Binding var sharedXRange: ClosedRange<Double> 
  
  @State var lastCount = 0
  
  class Coordinator: NSObject, ChartViewDelegate {
    var parent: CandleChartRepresentable
    
    init(parent: CandleChartRepresentable) {
      self.parent = parent
    }
    
    func chartTranslated(_ chartView: ChartViewBase, dX: CGFloat, dY: CGFloat) {
      print("chart translated")
      guard let candleStickChartView = chartView as? CandleStickChartView else { return }
      updateYAxis(for: candleStickChartView)
      parent.sharedXRange = candleStickChartView.lowestVisibleX...candleStickChartView.highestVisibleX
    }
    
    func chartViewDidEndPanning(_ chartView: ChartViewBase) {
      print("translating complete")
    }
    
    // 중복된 Y축 업데이트 로직을 함수로 분리
    func updateYAxis(for chartView: CandleStickChartView) {
      guard let dataSet = chartView.data?.dataSets.first as? CandleChartDataSet else { return }
      
      let lowestVisibleX = Int(chartView.lowestVisibleX)
      let highestVisibleX = Int(chartView.highestVisibleX)
      
      let (low, high) = dataSet.entries
        .compactMap { $0 as? CandleChartDataEntry }
        .filter { Int($0.x) >= lowestVisibleX && Int($0.x) <= highestVisibleX }
        .reduce((Double.greatestFiniteMagnitude, Double.leastNormalMagnitude)) { (minmax, entry) in
          let low = min(minmax.0, entry.low)
          let high = max(minmax.1, entry.high)
          return (low, high)
        }
      
      // Y축 범위를 설정
      chartView.leftAxis.axisMinimum = low - (high - low) * 0.1
      chartView.leftAxis.axisMaximum = high + (high - low) * 0.1
      chartView.rightAxis.enabled = true
      chartView.notifyDataSetChanged()
    }
  }
  
  func makeCoordinator() -> Coordinator {
    Coordinator(parent: self)
  }
  
  func makeNSView(context: Context) -> CandleStickChartView {
    let chartView = CandleStickChartView()
    chartView.delegate = context.coordinator
    chartView.chartDescription.enabled = false
    chartView.doubleTapToZoomEnabled = false
    chartView.pinchZoomEnabled = false
    chartView.dragEnabled = true
    chartView.setScaleEnabled(true)
    
    chartView.setVisibleXRangeMaximum(visibleCount)
    chartView.backgroundColor = .white
    chartView.legend.enabled = false
    
    chartView.leftAxis.enabled = false
    chartView.rightAxis.enabled = true
    chartView.rightAxis.minWidth = 60  // 동일한 너비로 고정 (적절한 값으로 조정)
    chartView.rightAxis.maxWidth = 60
    
    chartView.highlightPerTapEnabled = true
    chartView.highlightPerDragEnabled = false
    
    // X축
    let xAxis = chartView.xAxis
    xAxis.labelPosition = .bottom
    xAxis.granularity = 1 // X 값의 간격을 1로 설정
    xAxis.avoidFirstLastClippingEnabled = true // X 축의 첫/마지막 캔들이 겹치지 않게 설정
    xAxis.enabled = false
    
    let minuteFormatter = MinuteXAxisFormatter()
    xAxis.valueFormatter = minuteFormatter
    
    return chartView
  }
  
  func updateNSView(_ nsView: CandleStickChartView, context: Context) {
    guard let startDate = crypto?.startDate else {
      print("start Date가 없습니다.")
      return
    }
    
    (nsView.xAxis.valueFormatter as? MinuteXAxisFormatter)?.startDate = startDate
    
    let dataSet = CandleChartDataSet(entries: entries, label: "Candle Stick Data")
    dataSet.colors = [.blue] // 아무것도 없을 때
    dataSet.shadowColor = .darkGray
    dataSet.increasingColor = .blue
    dataSet.decreasingColor = .red
    dataSet.increasingFilled = true
    dataSet.decreasingFilled = true
    dataSet.drawValuesEnabled = false
    
    
    let candleData = CandleChartData(dataSet: dataSet)
    
    if entries.count > Int(visibleCount) {
      nsView.setVisibleXRangeMaximum(visibleCount)
      
      let nextX: Double
      if entries.count != lastCount {
        DispatchQueue.main.async {
          lastCount = entries.count
          sharedXRange = (sharedXRange.lowerBound + 1)...(sharedXRange.upperBound + 1)
        }
        nextX = sharedXRange.lowerBound + 1
      } else {
        nextX = sharedXRange.lowerBound
      }
      
      nsView.moveViewToX(nextX)
    }
    
    context.coordinator.updateYAxis(for: nsView)
    
    nsView.data = candleData
    nsView.notifyDataSetChanged()
  }
}


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
