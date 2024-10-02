import AppKit
import DGCharts
import SwiftUI

struct BarChartRepresentable: NSViewRepresentable {
  @State var id = UUID().uuidString
  
  @Binding var barEntries: [BarChartDataEntry]
  @Binding var candleEntries: [CandleChartDataEntry]
  @Binding var crypto: Crypto?
  @Binding var visibleCount: Double
  @Binding var sharedXRange: ClosedRange<Double>
  
  @State var lastCount = 0
  @Binding var draggingId: String?
  
  class Coordinator: NSObject, ChartViewDelegate {
    var parent: BarChartRepresentable
    var debounceTimer: Timer?
    
    init(parent: BarChartRepresentable) {
      self.parent = parent
    }
    
    func chartTranslated(_ chartView: ChartViewBase, dX: CGFloat, dY: CGFloat) {
      guard let barChartView = chartView as? BarChartView else { return }
      debounceTimer?.invalidate()
      
      parent.draggingId = parent.id
      parent.sharedXRange = barChartView.lowestVisibleX...barChartView.highestVisibleX
      
      debounceTimer = Timer.scheduledTimer(withTimeInterval: 0.001, repeats: false) { [weak self] _ in
        self?.parent.draggingId = nil
      }
    }
  }
  
  func makeCoordinator() -> Coordinator {
    Coordinator(parent: self)
  }
  
  func makeNSView(context: Context) -> BarChartView {
    let chartView = MouseTrackingBarChartView()
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
    chartView.rightAxis.setLabelCount(3, force: false)
    
    chartView.highlightPerTapEnabled = false
    
    // X축 설정 (여기서 X축을 표시)
    let xAxis = chartView.xAxis
    xAxis.labelPosition = .bottom
    xAxis.granularity = 1 // X 값의 간격을 1로 설정
    xAxis.avoidFirstLastClippingEnabled = true
    
    let minuteFormatter = MinuteXAxisFormatter()
    xAxis.valueFormatter = minuteFormatter
    
    return chartView
  }
  
  func updateNSView(_ nsView: BarChartView, context: Context) {
    guard let startDate = crypto?.startDate else {
      print("start Date가 없습니다.")
      return
    }
    
    (nsView.xAxis.valueFormatter as? MinuteXAxisFormatter)?.startDate = startDate
    
    let barDataSet = BarChartDataSet(entries: barEntries, label: "Bar Data")
//    barDataSet.colors = [.orange]
    barDataSet.drawValuesEnabled = false
    
    
    var barColors: [NSUIColor] = []
    for i in 0..<barEntries.count {
      let correspondingCandle = candleEntries[i] // 동일한 인덱스에 대응하는 캔들 차트 데이터
      
      // 캔들의 증가/감소 여부에 따라 색상을 설정
      if correspondingCandle.close >= correspondingCandle.open {
        barColors.append(.blue) // 캔들이 증가하는 경우 (예: 파란색)
      } else {
        barColors.append(.red)  // 캔들이 감소하는 경우 (예: 빨간색)
      }
    }
    
    barDataSet.colors = barColors
    
    let barData = BarChartData(dataSet: barDataSet)
    nsView.data = barData
    nsView.notifyDataSetChanged()
    
    var countChanged = false
    if lastCount != barEntries.count {
      DispatchQueue.main.async {
        lastCount = barEntries.count // in main thread
      }
      countChanged = true
    }
    
    guard barEntries.count > Int(visibleCount), draggingId != id else { return }
    
    // TODO: throttle
    
    nsView.setVisibleXRangeMaximum(visibleCount)
    
    let nextX: Double
    if draggingId != nil, draggingId != id {
      nextX = sharedXRange.lowerBound
    } else if draggingId == nil, countChanged {
      DispatchQueue.main.async {
//        lastCount = barEntries.count // in main thread
        sharedXRange = (sharedXRange.lowerBound + 1)...(sharedXRange.upperBound + 1)
      }
      nextX = sharedXRange.lowerBound + 1
    } else {
      return
    }

    nsView.moveViewToX(nextX)
  }
}
