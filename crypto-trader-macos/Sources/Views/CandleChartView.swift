import SwiftUI
import Charts

struct CandleChartView: View {
  
  @EnvironmentObject var candleViewModel: CandleViewModel
  @State private var dragOffset: CGFloat = 0
  @State private var totalDragOffset: CGFloat = 0
  
  // 뷰 모델로 빼기
  @State private var visibleCandles: [Candle] = []
  @State private var lastCount: Int = 0
  
  // 쓰로틀링
  @State private var lastDragTime = Date()
  
  let candlePadding: CGFloat = 6
  let candleWidth: CGFloat = 10
  var candleTotalWidth: CGFloat { candleWidth + candlePadding }
  
  var body: some View {
    GeometryReader { geo in
      let visibleWidth = geo.size.width - 30
      let totalWidth = CGFloat(candleViewModel.items.count) * candleTotalWidth
      let factor = (visibleWidth - totalWidth) / 2 - (candleWidth + candlePadding)
      let minOffset: CGFloat = min(0, (visibleWidth - totalWidth) / 2 + factor)
      let maxOffset: CGFloat = 0 // 항상 0
      
      HStack(spacing: 0) {
        chart
          .frame(width: totalWidth)
          .offset(x: totalDragOffset - factor - candlePadding)
          .gesture(
            DragGesture()
              .onChanged { value in
                let now = Date()
                if now.timeIntervalSince(lastDragTime) > 0.016 { // 16ms (60fps)
                  let proposedOffset = totalDragOffset + value.translation.width / 3 // 드래그 속도 조정
                  totalDragOffset = proposedOffset.clamped(to: minOffset...maxOffset)
                  lastDragTime = now
                }
                updateVisibleCandles(geo: geo)
              }
              .onEnded { value in
                totalDragOffset += value.translation.width / 3
                if totalDragOffset > maxOffset {
                  totalDragOffset = maxOffset
                } else if totalDragOffset < minOffset {
                  totalDragOffset = minOffset
                }
                updateVisibleCandles(geo: geo)
              }
          )
          .frame(width: visibleWidth, height: geo.size.height)
          .clipped()
        
        YAxisView(visibleCandles: $visibleCandles)
          .frame(width: 30) // 원하는 고정된 너비
          .background(Color.white) // 배경을 설정하여 Y축 레이블의 일관성을 유지
          .overlay(
            Rectangle()
              .frame(width: 1) // 경계선의 두께 설정
              .foregroundColor(.gray), // 경계선의 색상 설정
            alignment: .leading // 왼쪽에 경계선을 배치
          )
      }
      .frame(width: geo.size.width)
    }
    .background(.white)
    .onAppear {
      // 뷰가 나타날 때 초기 보이는 캔들 업데이트
      updateVisibleCandles(geo: nil)
      candleViewModel.fetchCandle(market: "KRW-BTC", unit: .one_minute)
    }
  }
  
  var chart: some View {
    let minPrice = candleViewModel.items.map { $0.low }.min() ?? 0
    let maxPrice = candleViewModel.items.map { $0.high }.max() ?? 100
    let adjustment: Double = 10 // 조정값
    
    return GeometryReader { geo in
      Chart(candleViewModel.items.indices, id: \.self) { index in
        let candle = candleViewModel.items[index]
        let normalizedOpen = ((candle.open - minPrice) / (maxPrice - minPrice)) * 100
        let normalizedClose = ((candle.close - minPrice) / (maxPrice - minPrice)) * 100 + (candle.open == candle.close ? adjustment: 0)
        let normalizedHigh = ((candle.high - minPrice) / (maxPrice - minPrice)) * 100
        let normalizedLow = ((candle.low - minPrice) / (maxPrice - minPrice)) * 100
        
        CandleStickMark(
          timestamp: .value("Date", candle.time),
          open: normalizedOpen,
          close: normalizedClose,
          high: .value("High", normalizedHigh),
          low: .value("Low", normalizedLow),
          red: candle.open >= candle.close
        )
      }
      .chartXAxis(.hidden)
      .chartYAxis(.hidden)
      // TODO: 이거 두 개 조합하면 어떻게든 안될까?
//      .chartYScale(range: <#T##PositionScaleRange#>)
//      .chartYVisibleDomain(length: <#T##Plottable & Numeric#>)
      .onReceive(candleViewModel.$items) { value in
        if value.count != lastCount {
          totalDragOffset -= candleTotalWidth
          lastCount = value.count
          
          print("last count: \(lastCount)")
        }
      }
      .id(lastCount)
    }
  }
  
  // 현재 보이는 캔들을 업데이트하는 함수
  func updateVisibleCandles(geo: GeometryProxy?) {
    guard let geo = geo else { return }
    
    let visibleWidth = geo.size.width - 30
    let startX = -totalDragOffset
    let endX = startX + visibleWidth
    
    // 시작 인덱스와 끝 인덱스를 계산하여 보이는 캔들 범위 결정
    let startIndex = max(Int(startX / candleTotalWidth), 0)
    let endIndex = min(Int((endX / candleTotalWidth).rounded(.up)), candleViewModel.items.count - 1)
    
    // 현재 보이는 캔들들
    // endIndex가. 마지막 - 2) 아래라면 - 2
    visibleCandles = Array(candleViewModel.items[startIndex...endIndex])
  }
  
  func getYScaleRange(geo: GeometryProxy) -> ClosedRange<CGFloat> {
    // 흠................................................
    return 0...0
  }
}


struct CandleStickMark: ChartContent {
  let timestamp: PlottableValue<Date>
  let open: Double
  let close: Double
  let high: PlottableValue<Double>
  let low: PlottableValue<Double>
  let red: Bool
    
  var body: some ChartContent {
    Plot {
      BarMark(
        x: timestamp,
        yStart: high,
        yEnd: low,
        width: 1
      )
      .cornerRadius(0)
      .foregroundStyle(.black)
      
      BarMark(
        x: timestamp,
        yStart: .value("Open", open),
        yEnd: .value("Close", close),
        width: 10
      )
      .cornerRadius(0)
      .foregroundStyle(red ? .red : .blue)
    }
  }
}

// TODO: adjust for visible candles
struct YAxisView: View {
  
  @Binding var visibleCandles: [Candle]
  
  var body: some View {
    VStack {
      Spacer()
      Text("200") // 최대값 예시
      Spacer()
      Text("150")
      Spacer()
      Text("100")
      Spacer()
      Text("50")
      Spacer()
      Text("0") // 최소값 예시
    }
  }
}

#Preview {
    CandleChartView()
}
