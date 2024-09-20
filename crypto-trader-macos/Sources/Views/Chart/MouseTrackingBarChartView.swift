import AppKit
import DGCharts

class MouseTrackingBarChartView: BarChartView {
  private var mouseTraker: ChartMouseTracker!
  
  override func viewDidMoveToSuperview() {
    super.viewDidMoveToSuperview()
    mouseTraker = ChartMouseTracker(chartView: self) { [weak self] highlight in
      self?.handleBarChartMouseMoved(highlight: highlight)
    }
  }
  
  override func updateTrackingAreas() {
    super.updateTrackingAreas()
    mouseTraker.updateTrackingArea()
  }
  
  override func mouseMoved(with event: NSEvent) {
    mouseTraker.handleMouseMoved(event: event)
  }
  
  override func mouseExited(with event: NSEvent) {
    mouseTraker.handleMouseExited()
  }
  
  private func handleBarChartMouseMoved(highlight: Highlight) {
    if let dataSet = self.data?.dataSets.first as? BarChartDataSet,
       let entry = dataSet.entryForXValue(highlight.x, closestToY: highlight.y) as? BarChartDataEntry {
      let yValue = entry.y
      self.highlightValue(highlight)
      
      mouseTraker.showLabel(at: yValue)
    }
  }
}
