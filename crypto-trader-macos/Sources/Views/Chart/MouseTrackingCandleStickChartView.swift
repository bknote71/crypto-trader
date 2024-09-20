import AppKit
import DGCharts

class MouseTrackingCandleStickChartView: CandleStickChartView {
  private var mouseTraker: ChartMouseTracker!
  
  override func viewDidMoveToSuperview() {
    super.viewDidMoveToSuperview()
    mouseTraker = ChartMouseTracker(chartView: self) { [weak self] highlight in
      self?.handleCandleStickMouseMoved(highlight: highlight)
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
  
  private func handleCandleStickMouseMoved(highlight: Highlight) {
    if let dataSet = self.data?.dataSets.first as? CandleChartDataSet,
       let entry = dataSet.entryForXValue(highlight.x, closestToY: highlight.y) as? CandleChartDataEntry {
      let middleY = (entry.open + entry.close) / 2.0
      let adjustedHighlight = Highlight(x: highlight.x, y: middleY, dataSetIndex: highlight.dataSetIndex)
      self.highlightValue(adjustedHighlight)
      
      mouseTraker.showLabel(at: middleY)
    }
  }
}
