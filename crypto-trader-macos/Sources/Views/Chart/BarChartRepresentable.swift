import AppKit
import DGCharts
import SwiftUI

struct BarChartRepresentable: NSViewRepresentable {
  @Binding var entries: [BarChartDataEntry]
  @Binding var crypto: Crypto?
  @Binding var visibleCount: Double
  @Binding var sharedXRange: ClosedRange<Double>
  @Binding var candleEntries: [CandleChartDataEntry]
  
  @State var lastCount = 0
  @State var isDragging = false
  
  class Coordinator: NSObject, ChartViewDelegate {
    var parent: BarChartRepresentable
    
    init(parent: BarChartRepresentable) {
      self.parent = parent
    }
    
    func chartTranslated(_ chartView: ChartViewBase, dX: CGFloat, dY: CGFloat) {
      guard let barChartView = chartView as? BarChartView else { return }
      parent.isDragging = true
      parent.sharedXRange = barChartView.lowestVisibleX...barChartView.highestVisibleX
    }
    
    func chartViewDidEndPanning(_ chartView: ChartViewBase) {
      parent.isDragging = false
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
    
    let barDataSet = BarChartDataSet(entries: entries, label: "Bar Data")
//    barDataSet.colors = [.orange]
    barDataSet.drawValuesEnabled = false
    
    
  
    var barColors: [NSUIColor] = []
    for i in 0..<entries.count {
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
    
    guard !isDragging, entries.count > Int(visibleCount) else { return }
    
    nsView.setVisibleXRangeMaximum(visibleCount)
    
    let nextX: Double
    if entries.count != lastCount {
      DispatchQueue.main.async {
        lastCount = entries.count
      }
      nextX = sharedXRange.lowerBound + 1
    } else {
      nextX = sharedXRange.lowerBound
    }
    
    nsView.moveViewToX(nextX)
  }
}
