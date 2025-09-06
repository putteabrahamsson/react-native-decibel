import AVFoundation 

class BackgroundSound {
    private var player: AVPlayer?

    // Spela lokal fil från bundle
    func playBackgroundSound(filePath: String) {
        guard let url = URL(string: filePath) else {
            print("Invalid file URI: \(filePath)")
            return
        }

        // AVAudioSession för bakgrundsuppspelning
        do {
            try AVAudioSession.sharedInstance().setCategory(.playAndRecord, mode: .default, options: [.mixWithOthers, .defaultToSpeaker])
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Failed to set audio session category: \(error)")
        }

        // Skapa AVPlayer och spela
        let item = AVPlayerItem(url: url)
        player = AVPlayer(playerItem: item)
        player?.play()
        print("Playing background sound: \(filePath)")
    }

    func pauseBackgroundSound() {
        player?.pause()
        print("Paused background sound")
    }

    func resumeBackgroundSound() {
        player?.play()
        print("Resumed background sound")
    }

    func stopBackgroundSound() {
        player?.pause()
        player = nil
        print("Stopped background sound")
    }
}
