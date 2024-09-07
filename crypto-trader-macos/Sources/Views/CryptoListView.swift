import SwiftUI

struct CryptoListView: View {
  @StateObject private var viewModel = CryptoListViewModel()
  
  var body: some View {
    VStack(spacing: 0) {
      searchBar
      header
      divider
      
      ScrollView {
        LazyVStack(spacing: 0) {
          ForEach(viewModel.items.indices, id: \.self) { index in
            let item = viewModel.items[index]
            CryptoListItemView(item: item)
            divider
          }
        }
      }
    }
    .frame(width: 400)
    .background(.white)
  }
  
  var searchBar: some View {
    HStack(spacing: 0) {
      TextField("코인명/심볼검색", text: .constant(""))
        .padding(8)
        .textFieldStyle(PlainTextFieldStyle())
      Image(systemName: "magnifyingglass")
        .resizable()
        .frame(width: 20, height: 20)
        .foregroundStyle(.blue)
    }
    .padding(.horizontal, 14)
    .padding(.vertical, 4)
    .background( // 위 아래 경계 추가 (전체는 overlay + Rectangle)
      VStack {
        divider
        Spacer()
        divider
      }
    )
  }
  
  var header: some View {
    HStack(spacing: 0) {
      Text("한글명")
        .frame(width: 150, alignment: .center)
      Text("현재가")
       .frame(width: 100, alignment: .center)
      Text("전일대비")
       .frame(width: 75, alignment: .center)
      Text("거래대금")
       .frame(width: 75, alignment: .center)
    }
    .padding(.vertical, 5)
  }
  
  var divider: some View {
    Color(.gray)
      .frame(height: 0.5)
      .padding(0)
  }
}

struct CryptoListItemView: View {
  var item: Crypto
  
  var body: some View {
    HStack(spacing: 0) {
      VStack(alignment: .leading, spacing: 0) {
        Text(item.nameKr)
          .font(.system(size: 14, weight: .bold))
        Text(item.nameEn)
          .font(.system(size: 12))
          .foregroundColor(.gray)
      }
      .frame(width: 140)
        
      Text(String(format: "%.2f", item.tradingPrice))
        .frame(width: 100, alignment: .trailing)
        .foregroundColor(item.change > 0 ? .red : .blue)
        
      VStack(alignment: .trailing, spacing: 0) {
        Text("\(item.change > 0 ? "+" : "-")\(item.change, specifier: "%.2f")%")
          .foregroundColor(item.change > 0 ? .red : .blue)
        Text("\(item.changeValue, specifier: "%.2f")")
          .foregroundColor(item.change > 0 ? .red : .blue)
          .font(.system(size: 12))
      }
      .frame(width: 75, alignment: .trailing)
      
      Text(item.volume)
        .frame(width: 75, alignment: .trailing)
        .padding(.trailing, 10)
    }
    .frame(width: 400, height: 40)
    .background(.white)
  }
}

#Preview {
  CryptoListView()
}

