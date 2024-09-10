import SwiftUI
import Charts
import DGCharts
import Foundation
import AppKit

struct CandleStickChartViewWrapper: NSViewRepresentable {
  
  var entries: [CandleChartDataEntry]
  var delegator = CandleStickChartViewDelegator()
  
  func makeNSView(context: Context) -> NSView {
    let mainView = NSView()
    
    let chartView = CandleStickChartView()
    chartView.data = generateChartData()
    
    chartView.dragEnabled = true
    chartView.doubleTapToZoomEnabled = false
    chartView.setScaleEnabled(true)
    
    // y축 설정
    chartView.leftAxis.enabled = false
    chartView.legend.enabled = false
    chartView.backgroundColor = .white
    
    chartView.rightAxis.enabled = true

    // 격자 없애기
    chartView.xAxis.drawGridLinesEnabled = false
    chartView.leftAxis.drawGridLinesEnabled = false
    chartView.rightAxis.drawGridLinesEnabled = false
    
    // X 축을 날짜로 설정
    let xAxis = chartView.xAxis
    xAxis.labelPosition = .bottom
    xAxis.valueFormatter = DateValueFormatter()
    
    // 초기에 최대 20개 항목 보이기
    chartView.setVisibleXRangeMaximum(50)
    
    // 델리게이터 설정
    chartView.delegate = delegator
    delegator.setChartView(chartView)
    
    // 캔들 클릭 시 하이라이트(십자가) 제거()
    chartView.highlightPerTapEnabled = false
    
    mainView.addSubview(chartView)
    chartView.frame = mainView.bounds
    chartView.autoresizingMask = [.width, .height]
    
    let overlayView = ClickHandlingOverlayView()
    overlayView.onClick = onCick
    overlayView.chartView = chartView
    
    mainView.addSubview(overlayView)
    overlayView.frame = mainView.bounds
    overlayView.autoresizingMask = [.width, .height]
    
    return mainView
  }
  
  func onCick(_ point: CGPoint) {
//    print("on click")
  }

  
  func updateNSView(_ nsView: NSView, context: Context) {
      // TODO
  }
  
  func generateChartData() -> CandleChartData {
    let dataSet = CandleChartDataSet(entries: entries, label: "Candlestick Data")
    dataSet.setColor(.blue)
    dataSet.shadowColor = .darkGray
    dataSet.decreasingColor = .red
    dataSet.increasingColor = .blue
    dataSet.decreasingFilled = true
    dataSet.increasingFilled = true
    dataSet.drawValuesEnabled = false
    dataSet.colors = [NSUIColor.clear] // 빈 데이터에 대해 클리어 컬러 사용
    //하이라이트
    dataSet.highlightColor = .black
    dataSet.highlightLineWidth = 1.5
    dataSet.highlightLineDashLengths = [4, 2]
    
    return CandleChartData(dataSet: dataSet)
  }
  
  func makeCoordinator() -> Coordinator {
      Coordinator(self)
  }
      
  class Coordinator: NSObject {
    var parent: CandleStickChartViewWrapper
    
    init(_ parent: CandleStickChartViewWrapper) {
        self.parent = parent
    }
  }
}

public class DateValueFormatter: NSObject, AxisValueFormatter {
  private let dateFormatter = DateFormatter()
  
  override init() {
    super.init()
    dateFormatter.dateFormat = "dd MMM HH:mm"
  }
  
  public func stringForValue(_ value: Double, axis: AxisBase?) -> String {
    return dateFormatter.string(from: Date(timeIntervalSince1970: value))
  }
}

class CandleStickChartViewDelegator: ChartViewDelegate {
  
  weak var chartView: CandleStickChartView?
  
  func setChartView(_ chartView: CandleStickChartView) {
    self.chartView = chartView
    updateVisibleYRange()
  }
  
  func chartTranslated(_ chartView: ChartViewBase, dX: CGFloat, dY: CGFloat) {
    updateVisibleYRange()
  }
  
  func updateVisibleYRange() {
    guard let chartView else { return }
    let xRangeStart = chartView.lowestVisibleX
    let xRangeEnd = chartView.highestVisibleX
    
    guard let dataSet = chartView.data?.dataSets.first as? CandleChartDataSet else { return }
    
    // 현재 화면에 보이는 데이터의 최소값과 최대값 계산
    let visibleEntries = dataSet.entries.filter { entry in
       return entry.x >= xRangeStart && entry.x <= xRangeEnd
   } as? [CandleChartDataEntry]
    
    guard let minY = visibleEntries?.min(by: { $0.low < $1.low })?.low,
          let maxY = visibleEntries?.max(by: { $0.high < $1.high })?.high else { return }

    // Y축의 최소 및 최대값 설정
    chartView.leftAxis.axisMinimum = minY
    chartView.leftAxis.axisMaximum = maxY
    chartView.rightAxis.axisMinimum = minY
    chartView.rightAxis.axisMaximum = maxY
    
    // 차트를 업데이트하여 반영
    chartView.notifyDataSetChanged()
  }
}

class ClickHandlingOverlayView: NSView {

  weak var chartView: NSView?
  var onClick: ((CGPoint) -> Void)?
    
  override func mouseDown(with event: NSEvent) {
    super.mouseDown(with: event)
    
    let clickLocation = event.locationInWindow
    onClick?(clickLocation) // 클릭 이벤트 처리
    
    if let panGesture = chartView?.gestureRecognizers.first(where: { $0 is NSPanGestureRecognizer }) as? NSPanGestureRecognizer {
      panGesture.mouseDown(with: event)
    } else {
        print("No suitable gesture recognizer found")
    }
  }

  override func mouseDragged(with event: NSEvent) {
    if let panGesture = chartView?.gestureRecognizers.first(where: { $0 is NSPanGestureRecognizer }) as? NSPanGestureRecognizer {
        panGesture.mouseDragged(with: event)
    } else {
        print("No suitable gesture recognizer found")
    }
  }

  override func mouseUp(with event: NSEvent) {
      // 드래그 후 이벤트 전달 종료
    if let panGesture = chartView?.gestureRecognizers.first(where: { $0 is NSPanGestureRecognizer }) as? NSPanGestureRecognizer {
      panGesture.mouseUp(with: event)
    }
  }
  
  override func mouseMoved(with event: NSEvent) {
    chartView?.mouseMoved(with: event)
  }
}


