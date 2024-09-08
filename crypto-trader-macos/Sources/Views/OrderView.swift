import SwiftUI

struct OrderView: View {
  @State private var selectedTab = "매수"
  
  var body: some View {
    VStack(spacing: 0) {
      HStack(spacing: 0) {
        tabButton(title: "매수", selectedTab: $selectedTab)
        tabButton(title: "매도", selectedTab: $selectedTab)
        tabButton(title: "간편주문", selectedTab: $selectedTab)
        tabButton(title: "거래내역", selectedTab: $selectedTab)
      }
      
      if selectedTab == "매수" {
        BuyView()
      } else {
        BuyView()
      }
      // 다른 탭에 대한 뷰는 여기에 추가하면 됩니다.
    }
    .frame(width: 460, height: 360)
    .padding(0)
    .background(.white)
  }
  
  func tabButton(title: String, selectedTab: Binding<String>) -> some View {
    let isSelected = selectedTab.wrappedValue == title
    return VStack(spacing: 12) {
      Text(title)
        .foregroundColor(isSelected ? .red : .black)
      Rectangle()
        .frame(height: 3)
        .foregroundColor(isSelected ? .red : .clear)
    }
    .padding(.top, 12)
    .contentShape(Rectangle())
    .onTapGesture {
      selectedTab.wrappedValue = title
    }
  }
}

struct BuyView: View {
  @State private var selectedOrderType = "지정가"
  @State private var totalAmount: String = "0"
  @State private var buyPrice: Int = 73800000
  @State private var orderAmount: String = "0"

  var body: some View {
    VStack(alignment: .leading, spacing: 0) {
      HStack(spacing: 0) {
        Text("주문유형")
          .padding(.trailing, 52)
        HStack(spacing: 8) {
          RadioButton(text: "지정가", selected: $selectedOrderType)
          RadioButton(text: "시장가", selected: $selectedOrderType)
          RadioButton(text: "예약-지정가", selected: $selectedOrderType)
        }
      }
      .padding(.vertical, 16)
      
      HStack(alignment: .center, spacing: 0) {
        Text("주문가능")
        Spacer()
        Text("0")
          .font(.headline)
          .padding(.trailing, 2)
        Text("KRW")
          .padding(0)
      }
      .padding(.top, 4)
      .padding(.bottom, 16)
      
      StepperView(value: $buyPrice, title: "매수가격 (KRW)")
        .padding(.bottom, 8)
      
      HStack(alignment: .center, spacing: 0) {
        Text("주문수량 (BTC)")
        Spacer()
        HStack {
          Spacer()
          Text("0")
            .padding(.trailing, 12)
        }
        .frame(width: 320, height: 40)
        .overlay(
          Rectangle()
            .stroke(Color.gray150, lineWidth: 1)
        )
      }
      .padding(.bottom, 8)
      
      HStack(spacing: 0) {
        Spacer()
        HStack(spacing: 12) {
          ForEach(["10%", "25%", "50%", "100%"], id: \.self) { percent in
            Button(action: {
              // 퍼센트에 맞는 수량 계산 코드 필요
            }) {
              Text(percent)
                .font(.system(size: 12))
                .padding(.vertical, 6)
                .padding(.horizontal, 12)
                .overlay {
                  Rectangle()
                    .stroke(Color.gray200, lineWidth: 1)
                }
            }
            .buttonStyle(PlainButtonStyle())
          }
          
          Button(action: {
            // 직접입력 기능 구현 필요
          }) {
            Text("직접입력")
              .font(.system(size: 12))
              .padding(.vertical, 6)
              .padding(.horizontal, 12)
              .overlay {
                Rectangle()
                  .stroke(Color.gray200, lineWidth: 1)
              }
          }
          .buttonStyle(PlainButtonStyle())
        }
        .padding(0)
        .frame(width: 320)
      }
      .padding(.bottom, 8)
      
      
      HStack(spacing: 0) {
        Text("주문총액 (KRW)")
        Spacer()
        HStack {
          Spacer()
          Text("0")
            .padding(.trailing, 12)
        }
        .frame(width: 320, height: 40)
        .overlay(
          Rectangle()
            .stroke(Color.gray150, lineWidth: 1)
        )
      }
      .frame(height: 40)
      .padding(.bottom, 8)
      
      Button {
        print("주문주문")
      } label: {
        Text("주문하기")
          .foregroundStyle(.white)
          .frame(maxWidth: .infinity)
          .frame(height: 40)
          .background(.red)
          .contentShape(Rectangle())
      }
      .buttonStyle(PlainButtonStyle())
    }
    .padding(.horizontal, 12)
    .padding(.bottom, 12)
  }
}


struct StepperView: View {
  @Binding var value: Int
  var title: String
    
  var body: some View {
    HStack(spacing: 0) {
      Text(title)
      Spacer()
      HStack(spacing: 0) {
        Spacer()
        Text("\(value)")
          .padding(.trailing, 12)
        
        Button(action: {
          value -= 1
        }) {
          Image(systemName: "minus")
            .frame(width: 40, height: 40)
            .background(Color.gray100)
        }
        .buttonStyle(BorderlessButtonStyle())
        
        Color(Color.gray200)
          .frame(width: 1)
        
        Button(action: {
          value += 1
        }) {
          Image(systemName: "plus")
            .frame(width: 40, height: 40)
            .background(Color.gray100)
        }
        .buttonStyle(BorderlessButtonStyle())
        .padding(0)
      }
      .padding(0)
      .frame(width: 320, height: 40)
      .overlay(
        Rectangle()
          .stroke(Color.gray150, lineWidth: 1)
      )
    }
  }
}

struct RadioButton: View {
    let text: String
    @Binding var selected: String
    
    var body: some View {
        Button(action: {
            selected = text
        }) {
            HStack {
                Image(systemName: selected == text ? "largecircle.fill.circle" : "circle")
                Text(text)
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}


#Preview {
  OrderView()
}
