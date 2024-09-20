import AppKit
import DGCharts

class ChartMouseTracker {
  private weak var chartView: BarLineChartViewBase?
  private var labelView: NSTextField
  private var mouseMovedCallback: ((Highlight) -> Void)?
  
  init(chartView: BarLineChartViewBase, mouseMovedCallback: @escaping (Highlight) -> Void) {
    self.chartView = chartView
    self.mouseMovedCallback = mouseMovedCallback
    self.labelView = NSTextField(labelWithString: "")
    setupLabelView()
    chartView.addSubview(labelView)
  }
  
  private func setupLabelView() {
    labelView.cell = CenteredTextFieldCell()
    labelView.isBezeled = false
    labelView.drawsBackground = true
    labelView.backgroundColor = NSColor.black
    labelView.textColor = NSColor.white
    labelView.font = NSFont.systemFont(ofSize: 10)
    labelView.alignment = .center
    labelView.frame.size = CGSize(width: 60, height: 20)
    labelView.isHidden = true
  }
  
  func updateTrackingArea() {
    guard let chartView = chartView else { return }
    chartView.addTrackingArea(NSTrackingArea(
      rect: chartView.bounds,
      options: [.mouseMoved, .mouseEnteredAndExited, .activeInKeyWindow],
      owner: chartView,
      userInfo: nil
    ))
  }
  
  func handleMouseMoved(event: NSEvent) {
    guard let chartView = chartView else { return }
    let location = event.locationInWindow
    let point = chartView.convert(location, from: nil)
    
    guard let highlight = chartView.getHighlightByTouchPoint(point) else {
      labelView.isHidden = true
      return
    }
    
    // 콜백을 통해 처리 로직을 위임
    mouseMovedCallback?(highlight)
  }
  
  func handleMouseExited() {
    hideHighlight()
  }
  
  func showLabel(at yValue: Double) {
    guard let chartView = chartView else { return }
    
    labelView.stringValue = String(Int(yValue))
    labelView.isHidden = false
    
    let yPosition = chartView.getTransformer(forAxis: .left).pixelForValues(x: 0, y: yValue).y
    let labelYPosition = yPosition - 20 / 2
    let labelXPosition = chartView.bounds.width - 60
    labelView.frame = CGRect(x: labelXPosition, y: labelYPosition, width: 60, height: 20)
  }
  
  func hideLabel() {
    labelView.isHidden = true
  }
  
  private func hideHighlight() {
    chartView?.highlightValue(nil) // 하이라이트 제거
    hideLabel()
  }
}

