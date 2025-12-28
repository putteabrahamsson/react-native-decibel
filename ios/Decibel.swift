import AVFoundation
import NitroModules

class Decibel: HybridDecibelSpec {
    private var audioRecorder: AVAudioRecorder?
    private var timer: Timer?
    private var decibelListeners: [(Double) -> Void] = []

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

    private func createNewRecorder() -> AVAudioRecorder? {
        let url = URL(fileURLWithPath: "/dev/null") 

        let settings: [String: Any] = [
            AVFormatIDKey: Int(kAudioFormatAppleLossless),
            AVSampleRateKey: 44100.0,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.max.rawValue
        ]

        do {
            let recorder = try AVAudioRecorder(url: url, settings: settings)
            recorder.isMeteringEnabled = true
            return recorder
        } catch {
            print("Error creating recorder: \(error)")
            return nil
        }
    }

    func start(interval: Double? = 0.2) {
        // Stop any existing recording
        stop()
        
        // Re-activate and configure audio session
        do {
            try AVAudioSession.sharedInstance().setCategory(.playAndRecord, mode: .default, options: [.mixWithOthers, .defaultToSpeaker])
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Error activating audio session: \(error)")
            return
        }
        
        // Create a fresh new recorder instance
        guard let recorder = createNewRecorder() else {
            print("Failed to create audio recorder")
            return
        }
        
        audioRecorder = recorder
        
        // Prepare and start recording
        audioRecorder?.prepareToRecord()
        audioRecorder?.record()

        timer = Timer.scheduledTimer(withTimeInterval: interval ?? 0.2, repeats: true) { [weak self] _ in
            guard let self = self, let recorder = self.audioRecorder else { return }
            recorder.updateMeters()
            let dB = Double(recorder.averagePower(forChannel: 0))

            for listener in self.decibelListeners {
              listener(dB)
            }
        }
    }

    func stop() {
        timer?.invalidate()
        timer = nil
        audioRecorder?.stop()
        audioRecorder = nil
    }
}

