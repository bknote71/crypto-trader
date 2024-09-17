import Combine
import Foundation

class CandleViewModel: ObservableObject {
  
  @Published var items = [Candle]()
  @Published var isFetched: Bool = false
  
  private let encoder = JSONEncoder()
  private let decoder: JSONDecoder = { // TODO: move to APIClient
    let decoder = JSONDecoder()
    decoder.dateDecodingStrategy = .custom { decoder in
      let container = try decoder.singleValueContainer()
      let dateString = try container.decode(String.self)
      
      // 지원하는 날짜 형식 리스트
      let dateFormats = [
        "yyyy-MM-dd'T'HH:mm:ss.SSSSS", // 밀리초/마이크로초 포함
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ssXXXXX"
      ]
      
      for format in dateFormats {
        let formatter = DateFormatter()
        formatter.dateFormat = format
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        
        if let date = formatter.date(from: dateString) {
          return date
        }
      }
      
      // 날짜가 형식에 맞지 않으면 nil 반환
      throw DecodingError.dataCorruptedError(in: container, debugDescription: "Date string does not match expected format.")
    }
    return decoder
  }()
  
  private var cancellableBag = Set<AnyCancellable>()
  
  private let candleWSClient = JsonWSClient<Candle>()
  
  init() {
    // TODO: 1. fetch all candle data(KRW-BTC)
    // fetchAllCandles(market: "KRW-BTC", unit: .one_minute)
    
    // TODO: 2. connect
    connect()
  }
  
  public func fetchAllCandles(market: String, unit: CandleUnit) {
    guard let allCandlesUrl = APIEndpoint.allCandles.url else { return }
    print("fetch all candles: \(market)")
    APIClient.shared.request(url: allCandlesUrl, param: ["market": market])
      .filter { (_, response) in response.statusCode < 300 }
      .map(\.0)
      .decode(type: [Candle].self, decoder: decoder)
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] candles in
        guard let self else { return }
        print("초기화!")
        items = [Candle]() // 초기화
        candles.forEach { self.appendCandle($0) }
        
        connect()
        send(market: market, unit: unit)
        
        isFetched = true
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
    
    let lastX = items.last?.x ?? Date.now
    let newCandle = Candle(
      open: candle.open,
      close: candle.close,
      high: candle.high,
      low: candle.low,
      time: candle.time,
      x: lastX + TimeInterval(60)
    )
    
    items.append(newCandle)
  }
}
