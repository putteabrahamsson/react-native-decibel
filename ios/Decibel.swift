import AVFoundation
import NitroModules

class Decibel: HybridDecibelSpec {
    private var audioRecorder: AVAudioRecorder!
    private var timer: Timer?
    private var decibelListeners: [(Double) -> Void] = []
    
    override init() {
      super.init()
      setupRecorder()
    }

    public func onDecibelUpdate(listener: @escaping (Double) -> Void) {
        decibelListeners.append(listener)
    }

    public func removeDecibelUpdateListener(listener: @escaping (Double) -> Void) {
        decibelListeners.removeAll()
    }

    // Request microphone permission
        public func requestPermission() throws -> Promise<String> {
            return Promise.async {
            var permission = "undetermined"

            AVAudioSession.sharedInstance().requestRecordPermission { granted in
                if granted {
                    permission = "granted"
                } else {
                    permission = "not_granted"
                }
            }

            return permission
        }
    }

    private func setupRecorder() {
        let url = URL(fileURLWithPath: "/dev/null") 

        let settings: [String: Any] = [
            AVFormatIDKey: Int(kAudioFormatAppleLossless),
            AVSampleRateKey: 44100.0,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.max.rawValue
        ]

        do {
            audioRecorder = try AVAudioRecorder(url: url, settings: settings)
            audioRecorder.isMeteringEnabled = true
            try AVAudioSession.sharedInstance().setCategory(.playAndRecord, mode: .default, options: [.mixWithOthers, .defaultToSpeaker])
            try AVAudioSession.sharedInstance().setActive(true)
            audioRecorder.prepareToRecord()
        } catch {
            print("Error setting up recorder: \(error)")
        }
    }

    func start(interval: Double? = 0.2) {
        audioRecorder.record()

        timer = Timer.scheduledTimer(withTimeInterval: interval ?? 0.2, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            self.audioRecorder.updateMeters()
            let dB = Double(self.audioRecorder.averagePower(forChannel: 0))

            for listener in self.decibelListeners {
              listener(dB)
            }
        }
    }

    func stop() {
        timer?.invalidate()
        audioRecorder.stop()
    }
}

