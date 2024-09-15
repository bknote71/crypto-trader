import AppKit
import SwiftUI

struct DoubleTextField: NSViewRepresentable {
  @Binding var value: Double
  var isEditing: Bool
  
  func makeNSView(context: Context) -> NSTextField {
    let textField = NSTextField()
    textField.isBezeled = false // 테두리 제거
    textField.drawsBackground = false // 배경 제거
    textField.focusRingType = .none // 포커스 링 제거
    textField.alignment = .right
    textField.delegate = context.coordinator
    return textField
  }
  
  func updateNSView(_ nsView: NSTextField, context: Context) {
    nsView.stringValue = String(value)
  }
  
  func makeCoordinator() -> Coordinator {
    Coordinator(self)
  }
  
  class Coordinator: NSObject, NSTextFieldDelegate {
    var parent: DoubleTextField
    
    init(_ parent: DoubleTextField) {
      self.parent = parent
    }
    
    func controlTextDidChange(_ obj: Notification) {
      parent.isEditing = false
      guard let textField = obj.object as? NSTextField else { return }
      // Double로 변환 가능한 입력만 반영
      if let doubleValue = Double(textField.stringValue) {
        parent.value = doubleValue
      } else {
        // 입력이 Double 형이 아니면 현재 값 유지
        textField.stringValue = String(parent.value)
      }
    }
    
    func controlTextDidEndEditing(_ obj: Notification) {
      parent.isEditing = false
      if let textField = obj.object as? NSTextField {
        // 마지막 입력값 검증
        parent.value = Double(textField.stringValue) ?? parent.value
      }
    }
  }
}
