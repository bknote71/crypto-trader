import SwiftUI

struct OrderListView: View {
  var body: some View {
    orderListHeader
    Color(Color.gray200)
      .frame(height: 1)
    orderListBody
  }
  
  @State var iselectButton: String = "1주일"
  @State var tselectButton: String = "전체"
  
  func buttons(_ labels: [String], _ selectButton: Binding<String>) -> some View {
    HStack(spacing: 0) {
      ForEach(labels.indices, id: \.self) { index in
        let label = labels[index]
        Button {
          selectButton.wrappedValue = label
        } label: {
          Text(label)
            .foregroundStyle(
              selectButton.wrappedValue == label ? .blue : .gray
            )
            .padding(.vertical, 8)
            .padding(.horizontal, 12)
            .background(.white)
        }
        .buttonStyle(PlainButtonStyle())
        .overlay {
          Rectangle()
            .stroke(
              selectButton.wrappedValue == label ? .blue : Color.black.opacity(0.1),
              lineWidth: 1
            )
        }
        .contentShape(Rectangle())
      }
    }
  }
  
  let intervalLables = ["1주일", "1개월", "3개월", "6개월", "직접입력"]
  let typeLabels = ["전체", "매수", "매도", "입금", "출금"]
  
  var orderListHeader: some View {
    HStack(alignment: .top) {
      VStack(alignment: .leading, spacing: 8) {
        Text("기간 2021.09.01 ~. 024.09.02")
        buttons(intervalLables, $iselectButton)
      }
      
      Spacer()
      
      VStack(alignment: .leading, spacing: 8) {
        Text("종류")
        buttons(typeLabels, $tselectButton)
      }
      
      Spacer()
      
      VStack(alignment: .leading, spacing: 8) {
        Text("코인선택")
        DoubleTextField(value: .constant(1.0), isEditing: true)
          .overlay {
            Rectangle()
              .stroke(.blue)
          }
          .frame(width: 250)
      }
    }
    .padding(24)
  }
  
  // 10가지 그리드
  let orderHeaderColumns: [GridItem] = Array(
    repeating: GridItem(.flexible(), spacing: 0, alignment: .center),
    count: 10
  )
  
  let orderDataColumns: [GridItem] = [
    GridItem(.flexible(), spacing: 0, alignment: .leading),
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .trailing),
    GridItem(.flexible(), spacing: 0, alignment: .trailing),
    GridItem(.flexible(), spacing: 0, alignment: .trailing),
    GridItem(.flexible(), spacing: 0, alignment: .trailing),
    GridItem(.flexible(), spacing: 0, alignment: .trailing),
    GridItem(.flexible(), spacing: 0, alignment: .trailing),
  ]
  
  let tempData: [OrderInfo] = [
    OrderInfo(
      executionTime: "2022.02.11",
      crypto: "LINK",
      market: "KRW",
      side: "매수",
      amount: "20.00",
      price: "21090",
      total: "422377",
      fee: "211.1",
      finalValue: "422528",
      orderTime: "2022.02.11"
    ),
    OrderInfo(
      executionTime: "2022.02.11",
      crypto: "LINK",
      market: "KRW",
      side: "매수",
      amount: "20.00",
      price: "21090",
      total: "422377",
      fee: "211.1",
      finalValue: "422528",
      orderTime: "2022.02.11"
    )
  ]
  
  var orderListBody: some View {
    VStack(alignment: .leading, spacing: 0) {
      LazyVGrid(columns: orderHeaderColumns) {
        Text("체결시간").bold()
        Text("코인").bold()
        Text("마켓").bold()
        Text("종류").bold()
        Text("거래수량").bold()
        Text("거래단가").bold()
        Text("거래금액").bold()
        Text("수수료").bold()
        Text("정산금액").bold()
        Text("주문시간").bold()
      }
      .frame(height: 25)
      .background(Color.gray50)
      
      Divider()
      
      ForEach(tempData) { order in
        LazyVGrid(columns: orderDataColumns) {
          Text(order.executionTime)
          
          Text(order.crypto)
          
          Text(order.market)
          
          Text(order.side)
          
          Text(order.amount)
          
          Text(order.price)
          
          Text(order.fee)
          
          Text(order.total)
          
          Text(order.finalValue)
          
          Text(order.orderTime)
        }
        .frame(height: 40)
        .padding(.horizontal, 16)
      }
      
      Color(Color.gray100)
        .frame(height: 1)
    }
  }
}

#Preview {
    OrderListView()
}
