import SwiftUI
import Combine

class SearchViewModel: ObservableObject {
  @Published var searchText: String = ""
  @Published var debouncedText: String = ""
  
  private var cancellables = Set<AnyCancellable>()

  init() {
    $searchText
      .debounce(for: .milliseconds(100), scheduler: RunLoop.main)
      .sink { [weak self] text in
        self?.debouncedText = text
      }
      .store(in: &cancellables)
  }
}
