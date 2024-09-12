import SwiftUI

struct CandleData {
    var date: Date
    var open: Double
    var high: Double
    var low: Double
    var close: Double
}

struct CandleStickChartView: View {
    var data: [CandleData]
    
    var body: some View {
        GeometryReader { geometry in
            Canvas { context, size in
                let width = size.width / CGFloat(data.count)
                
                for (index, candle) in data.enumerated() {
                    let xPosition = CGFloat(index) * width
                    let openY = mapToY(candle.open, in: size.height)
                    let closeY = mapToY(candle.close, in: size.height)
                    let highY = mapToY(candle.high, in: size.height)
                    let lowY = mapToY(candle.low, in: size.height)
                    
                    let color: Color = candle.close >= candle.open ? .blue : .red
                    
                    // Draw high-low line
                    context.stroke(
                        Path { path in
                            path.move(to: CGPoint(x: xPosition + width / 2, y: highY))
                            path.addLine(to: CGPoint(x: xPosition + width / 2, y: lowY))
                        },
                        with: .color(.gray),
                        lineWidth: 1
                    )
                    
                    // Draw open-close rectangle
                    let rectY = min(openY, closeY)
                    let rectHeight = abs(openY - closeY)
                    let rect = CGRect(x: xPosition, y: rectY, width: width * 0.8, height: rectHeight)
                    context.fill(Path(rect), with: .color(color))
                }
            }
        }
        .padding()
        .frame(height: 300)
    }
    
    // Map value to Y position in the chart
    private func mapToY(_ value: Double, in height: CGFloat) -> CGFloat {
        guard let max = data.max(by: { $0.high < $1.high })?.high,
              let min = data.min(by: { $0.low < $1.low })?.low else {
            return 0
        }
        let range = max - min
        return height * CGFloat(1 - (value - min) / range)
    }
}

struct ContentView: View {
    var body: some View {
        let sampleData = [
            CandleData(date: Date(), open: 100, high: 110, low: 90, close: 105),
            CandleData(date: Date(), open: 105, high: 115, low: 95, close: 100),
            CandleData(date: Date(), open: 100, high: 120, low: 85, close: 110),
            CandleData(date: Date(), open: 110, high: 125, low: 105, close: 120),
            CandleData(date: Date(), open: 120, high: 130, low: 115, close: 125),
        ]
        
        CandleStickChartView(data: sampleData)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

