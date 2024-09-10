import Combine
import DGCharts
import Foundation

class CandleViewModel: ObservableObject {
  
  @Published var items: [CandleChartDataEntry]
  
  private var entries = [CandleChartDataEntry]()
  
  private let encoder = JSONEncoder()
  private var cancellableBag = Set<AnyCancellable>()
  
  private let candleWebSocketManager = JsonWebSocketManager<Candle>()
  
  init() {
    items = [CandleChartDataEntry]()
    self.items = emptyData()
    // TODO: 1. fetch all candle data(KRW-BTC)
  }
  
  public func fetchCandle(market: String, unit: CandleUnit) {
    // unit: ONEMINUTE, FIVEMINUTE
    if !candleWebSocketManager.isConnected {
      let candleUrl = "ws://127.0.0.1:8090/candle"
      candleWebSocketManager.connect(url: candleUrl)
        .receive(on: RunLoop.main)
        .sink { [weak self] candle in
          guard let self else { return }
          
          let entry = CandleChartDataEntry(
            x: Double(entries.count + 1),
            shadowH: candle.high,
            shadowL: candle.low,
            open: candle.open,
            close: candle.close
          )
          
          print("append new candle entry \(entry)")
          entries.append(entry)
          items = entries
        }
        .store(in: &cancellableBag)
    }
    
    let dto = CandleRequestDto(market: market, unit: unit)
    do {
      let data = try encoder.encode(dto)
      guard let message = String(data: data, encoding: .utf8) else {
        // TODO: throw err
        return
      }
      
      candleWebSocketManager.sendMessage(message)
    } catch {
      print("err: \(error)")
    }
  }
  
  func emptyData() -> [CandleChartDataEntry] {
    let lastX = entries.last?.x ?? 0
    var dummy = [CandleChartDataEntry]()
    for i in 1...5 {
      let emptyEntry = CandleChartDataEntry(x: lastX + Double(i), shadowH: 0, shadowL: 0, open: 0, close: 0)
      dummy.append(emptyEntry)
    }
    return dummy
  }
  
  static func dummyData() -> [CandleChartDataEntry] {
    var dummy = [CandleChartDataEntry]()
    var previousClose: Double = 100.0
    let emptyIndices: Set<Int> = Set(75...80)
    
    for i in 1...80 {
      if emptyIndices.contains(i) {
        let emptyEntry = CandleChartDataEntry(x: Double(i), shadowH: 0, shadowL: 0, open: 0, close: 0)
        dummy.append(emptyEntry)
        continue
      }
      
      let high = previousClose + Double.random(in: 0...30)
      let low = previousClose - Double.random(in: 0...30)
      let close = Double.random(in: low...high)
      let open = previousClose // open은 이전 close 값
      
      // 새로운 CandleChartDataEntry 생성
      let entry = CandleChartDataEntry(x: Double(i), shadowH: high, shadowL: low, open: open, close: close)
      
      // 엔트리를 배열에 추가
      dummy.append(entry)
      
      // 현재 close 값을 다음 루프에서 사용할 수 있도록 저장
      previousClose = close
    }
    
    return dummy
  }
}
