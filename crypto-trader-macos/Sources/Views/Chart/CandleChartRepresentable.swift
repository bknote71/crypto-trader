import Charts
import DGCharts
import SwiftUI

struct CandleChartRepresentable: NSViewRepresentable {
  @State var id = UUID().uuidString
  
  @Binding var entries: [CandleChartDataEntry]
  @Binding var crypto: Crypto?
  @Binding var visibleCount: Double
  @Binding var sharedXRange: ClosedRange<Double> 
  
  @State var lastCount = 0
  @State var firstDragging = true
  @Binding var draggingId: String?
  
  var candleViewModel: CandleViewModel
  
  class Coordinator: NSObject, ChartViewDelegate {
    var parent: CandleChartRepresentable
    var debounceTimer: Timer?
    
    init(parent: CandleChartRepresentable) {
      self.parent = parent
    }
    
    func chartTranslated(_ chartView: ChartViewBase, dX: CGFloat, dY: CGFloat) {
      guard let candleStickChartView = chartView as? CandleStickChartView else { return }
      debounceTimer?.invalidate()
      
      parent.firstDragging = false
      parent.draggingId = parent.id
      parent.sharedXRange = candleStickChartView.lowestVisibleX...candleStickChartView.highestVisibleX
      
      parent.candleViewModel.fetchCandlesBasedOnX(candleStickChartView.lowestVisibleX)
      
      updateYAxis(for: candleStickChartView)
      
      debounceTimer = Timer.scheduledTimer(withTimeInterval: 0.001, repeats: false) { [weak self] _ in
        self?.parent.draggingId = nil
      }
    }
    
    // 중복된 Y축 업데이트 로직을 함수로 분리
    func updateYAxis(for chartView: CandleStickChartView) {
      guard let dataSet = chartView.data?.dataSets.first as? CandleChartDataSet else { return }
      
      let lowest = chartView.lowestVisibleX
      let highest = chartView.highestVisibleX
      
      let lowestVisibleX = lowest > Double(Int.max) || lowest < Double(Int.min) ? 0 : Int(lowest)
      let highestVisibleX = highest > Double(Int.max) || highest < Double(Int.min) ? 30 : Int(highest)
      
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
    let chartView = MouseTrackingCandleStickChartView()
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
//    xAxis.enabled = false
    xAxis.drawLabelsEnabled = false
    
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
    
    let dataSet = CandleChartDataSet(entries: Array(entries), label: "Candle Stick Data")
    dataSet.colors = [.clear] // 아무것도 없을 때
    dataSet.shadowColor = .darkGray
    dataSet.increasingColor = .blue
    dataSet.decreasingColor = .red
    dataSet.increasingFilled = true
    dataSet.decreasingFilled = true
    dataSet.drawValuesEnabled = false
    
    // highlight
    dataSet.highlightColor = .black
    
    let candleData = CandleChartData(dataSet: dataSet)
    nsView.data = candleData
    nsView.notifyDataSetChanged()
    
    var countChanged = false
    if lastCount != entries.count {
      DispatchQueue.main.async {
        lastCount = entries.count // in main thread
      }
      countChanged = true
    }
    
    guard entries.count > Int(visibleCount), draggingId != id else { return }
    
    // TODO: throttle
    
    nsView.setVisibleXRangeMaximum(visibleCount)
    
    let nextX: Double
    if firstDragging {
      nextX = entries.last?.x ?? 0
    } else if draggingId != nil, draggingId != id {
      nextX = sharedXRange.lowerBound
    } else if draggingId == nil, countChanged {
      DispatchQueue.main.async {
        // ?
      }
      nextX = sharedXRange.lowerBound + 1
    } else {
      return
    }
    
    nsView.moveViewToX(nextX)
    context.coordinator.updateYAxis(for: nsView)
  }
}
