import SwiftUI
import UIKit
import ComposeApp

private func tmdbAccessToken() -> String {
    let environmentToken = ProcessInfo.processInfo.environment["TMDB_ACCESS_TOKEN"]?
        .trimmingCharacters(in: .whitespacesAndNewlines)
    if let environmentToken, !environmentToken.isEmpty {
        return environmentToken
    }

    guard let tokenURL = Bundle.main.url(forResource: "tmdb-access-token", withExtension: "txt"),
          let token = try? String(contentsOf: tokenURL, encoding: .utf8) else {
        return ""
    }
    return token.trimmingCharacters(in: .whitespacesAndNewlines)
}

struct BestvComposeHost: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            initialUrl: nil,
            accessToken: tmdbAccessToken(),
            onOpenExternalUrl: { value in
                guard let url = URL(string: value) else { return }
                UIApplication.shared.open(url)
            },
            onExit: {}
        )
    }

    func updateUIViewController(_ controller: UIViewController, context: Context) {}
}

@main
struct BESTVApp: App {
    var body: some Scene {
        WindowGroup {
            BestvComposeHost()
                .ignoresSafeArea()
                .onOpenURL { url in
                    BestvAppKt.handleBestvDeepLink(uri: url.absoluteString)
                }
        }
    }
}
