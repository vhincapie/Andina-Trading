export default function SuccessAlert({ message, onClose }) {
  if (!message) return null;

  return (
    <div
      className="rounded border border-green-300 bg-green-50 text-green-800 px-3 py-2 relative"
      style={{ marginBottom: "8px" }}
    >
      <span>{message}</span>
      {onClose && (
        <button
          onClick={onClose}
          className="absolute right-2 top-1 text-green-600 hover:text-green-800"
        >
          ×
        </button>
      )}
    </div>
  );
}
