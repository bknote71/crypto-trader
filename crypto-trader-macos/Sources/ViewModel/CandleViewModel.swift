import Combine
import DGCharts
import Foundation

class CandleViewModel: ObservableObject {
  
  @Published var candleEntries = [CandleChartDataEntry]()
  @Published var barEntries = [BarChartDataEntry]()
  
  private var items = [Candle]()
  
  private let encoder = JSONEncoder()
  
  private var cancellableBag = Set<AnyCancellable>()
  
  private let candleAPIClient = JsonAPIClient<[Candle]>()
  private let candleWSClient = JsonWSClient<Candle>()
  
  init() {
    // TODO: 1. fetch all candle data(KRW-BTC)
    // empty data?
     // fetchAllCandles(market: "KRW-BTC", unit: .one_minute)
  }
  
  public func fetchAllCandles(market: String, unit: CandleUnit) {
    guard let allCandlesUrl = APIEndpoint.allCandles.url else { return }
    candleAPIClient.request(url: allCandlesUrl, param: ["market": market])
      .filter { (_, response) in response.statusCode < 300 }
      .map(\.0)
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] candles in
        guard let self, let candles else { return }
        print("fetch all candles from api \(Date.now) \(candles.count)")
        candleEntries = []
        barEntries = []
        items = []
        
        candles.forEach { self.appendCandle($0) }
        
        connect()
        send(market: market, unit: unit)
      }
      .store(in: &cancellableBag)
  }
  
  private func connect() {
    guard  !candleWSClient.isConnected,
           let candleUrl = WSEndpoint.candle.url
    else { return }
    
    candleWSClient.connect(url: candleUrl)
      .receive(on: RunLoop.main)
      .sink { [weak self] candle in
        guard let self else { return }
        appendCandle(candle)
      }
      .store(in: &cancellableBag)
  }
  
  private func send(market: String, unit: CandleUnit) {
    let dto = CandleRequestDto(market: market, unit: unit)
    do {
      let data = try encoder.encode(dto)
      guard let message = String(data: data, encoding: .utf8) else {
        // TODO: throw err
        return
      }
      
      candleWSClient.sendMessage(message)
    } catch {
      print("err: \(error)")
    }
  }
  
  private func appendCandle(_ candle: Candle) {
    guard candle.time != items.last?.time else { return }

    let newCandle = Candle(
      open: candle.open,
      close: candle.close,
      high: candle.high,
      low: candle.low,
      time: candle.time,
      volume: candle.volume
    )
    
    let newCandleEntry = CandleChartDataEntry(x: Double(candleEntries.count),
                                        shadowH: candle.high,
                                        shadowL: candle.low,
                                        open: candle.open,
                                        close: candle.close)
    let newBarEntry = BarChartDataEntry(x: Double(barEntries.count),
                                        y: min(1000, max(candle.volume * 1000, 5)))
    
    candleEntries.append(newCandleEntry)
    barEntries.append(newBarEntry)
    items.append(newCandle)
  }
}
