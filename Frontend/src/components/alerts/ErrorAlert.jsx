export default function ErrorAlert({ message, onClose }) {
  if (!message) return null;

  return (
    <div
      className="relative rounded-2xl border border-rose-800/50 bg-rose-900/40 text-rose-300 px-4 py-3 shadow-lg ring-1 ring-white/10 backdrop-blur"
      style={{ marginBottom: "10px" }}
    >
      <span className="block text-sm font-medium">{message}</span>
      {onClose && (
        <button
          onClick={onClose}
          className="absolute right-3 top-2 text-rose-400 hover:text-rose-200 transition-colors text-lg leading-none"
        >
          ×
        </button>
      )}
    </div>
  );
}
