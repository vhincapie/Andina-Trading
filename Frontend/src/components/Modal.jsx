export default function Modal({ open, title, children, onClose, footer }) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div
        className="absolute inset-0 bg-slate-950/70 backdrop-blur-sm"
        onClick={onClose}
        aria-hidden
      />
      <div
        className="relative w-[92%] max-w-3xl max-h-[85vh] overflow-hidden
                      rounded-2xl bg-slate-900/60 text-slate-100
                      border border-slate-800 ring-1 ring-white/10
                      shadow-2xl"
      >
        <div className="px-5 py-4 border-b border-slate-800">
          <h3 className="text-lg font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            {title}
          </h3>
        </div>
        <div className="p-5 overflow-auto" style={{ maxHeight: "60vh" }}>
          {children}
        </div>
        {footer && (
          <div className="px-5 py-4 border-t border-slate-800 bg-slate-900/70">
            {footer}
          </div>
        )}
      </div>
    </div>
  );
}
