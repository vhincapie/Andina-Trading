import { useEffect, useState } from "react";
import {
  registrarContrato,
  getMiContratoActivo,
  cancelarMiContratoActivo,
} from "../../api/contratoService";
import { listarComisionistas } from "../../api/comisionistaService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";
import Modal from "../../components/Modal";

export default function ContratosInversionistaPage() {
  const [contrato, setContrato] = useState(null);
  const [comisionistas, setComisionistas] = useState([]);
  const [selectedComisionistaId, setSelectedComisionistaId] = useState("");
  const [moneda, setMoneda] = useState("COP");
  const [observaciones, setObservaciones] = useState("");
  const [contratoLoading, setContratoLoading] = useState(true);
  const [contratoSaving, setContratoSaving] = useState(false);

  const [showTerms, setShowTerms] = useState(false);
  const [acceptedTerms, setAcceptedTerms] = useState(false);

  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  const porcentajeDefault = Number(
    import.meta.env.VITE_CONTRATOS_PORCENTAJE_DEFAULT ?? 2.5
  );

  const reloadContrato = async () => {
    setContratoLoading(true);
    try {
      const c = await getMiContratoActivo().catch(() => null);
      setContrato(c);
    } finally {
      setContratoLoading(false);
    }
  };

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 6000);
    return () => clearTimeout(t);
  }, [error, ok]);

  useEffect(() => {
    (async () => {
      try {
        const lista = await listarComisionistas().catch(() => []);
        setComisionistas(lista || []);
      } catch (e) {
        console.error(e);
        setError("No fue posible cargar la lista de comisionistas.");
      }
      await reloadContrato();
    })();
  }, []);

  const buildTerminos = () => {
    const raw = `
ANDINA TRADING S.A.S. – TÉRMINOS Y CONDICIONES DEL CONTRATO DE COMISIONAMIENTO

1. Partes.
Intervienen: (i) ANDINA TRADING S.A.S. (“Andina Trading”), en calidad de comisionista, a través del comisionista seleccionado por el inversionista, y (ii) el Inversionista identificado en la orden de registro realizada en la plataforma.

2. Objeto.
El Comisionista realizará, por cuenta y riesgo del Inversionista, actividades de intermediación y ejecución de órdenes de inversión conforme a las instrucciones del Inversionista y a las políticas internas de Andina Trading.

3. Honorarios.
El Inversionista reconoce y acepta un honorario estándar equivalente al {{PORCENTAJE}}% sobre el valor administrado y/o resultados según aplique, cobrado con la periodicidad definida por Andina Trading.

4. Moneda y cargos.
La moneda del contrato será {{MONEDA}}. El Inversionista asume costos, impuestos, comisiones bancarias, cambiarias y demás cargos aplicables por terceros.

12. Terminación.
Cualquiera de las partes podrá terminar el contrato en cualquier momento con aviso por los canales oficiales.

15. Aceptación.
Al marcar “Acepto los términos” y registrar la orden en la plataforma, el Inversionista declara haber leído y aceptado íntegramente este contrato.
    `
      .replaceAll("{{PORCENTAJE}}", String(porcentajeDefault))
      .replaceAll("{{MONEDA}}", moneda);

    return raw.trim();
  };

  const onRegistrarContrato = async () => {
    setError("");
    setOk("");
    if (!selectedComisionistaId)
      return setError("Debes seleccionar un comisionista.");
    if (!acceptedTerms)
      return setError("Debes aceptar los términos del contrato.");
    setContratoSaving(true);
    try {
      const payload = {
        comisionistaId: Number(selectedComisionistaId),
        moneda,
        observaciones: (observaciones || "").trim(),
        aceptaTerminos: true,
      };
      await registrarContrato(payload);
      await reloadContrato();

      window.dispatchEvent(new Event("contract:changed"));
      setOk("Contrato registrado con éxito.");
      setSelectedComisionistaId("");
      setObservaciones("");
      setAcceptedTerms(false);
    } catch (e) {
      setError(
        e?.response?.data?.message || "No fue posible registrar el contrato."
      );
    } finally {
      setContratoSaving(false);
    }
  };

  const onCancelarContrato = async () => {
    setError("");
    setOk("");
    if (!contrato || contrato.estado !== "ACTIVO") return;
    setContratoSaving(true);
    try {
      await cancelarMiContratoActivo();
      await reloadContrato();

      window.dispatchEvent(new Event("contract:changed"));
      setOk("Contrato cancelado.");
      setContrato(null);
    } catch (e) {
      setError(
        e?.response?.data?.message || "No fue posible cancelar el contrato."
      );
    } finally {
      setContratoSaving(false);
    }
  };

  const shell = "min-h-[100dvh] bg-slate-950 text-slate-100";
  const wrap = "max-w-7xl mx-auto px-4 md:px-6 py-6 md:py-8";
  const panel =
    "bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur p-6 md:p-7";
  const label = "text-xs uppercase tracking-wide text-slate-400";
  const input =
    "w-full bg-slate-900/60 border border-slate-700/70 rounded-lg px-3 py-2 text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-400/60 focus:border-emerald-400/40";
  const btnPrimary =
    "bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2.5 rounded-lg transition-colors disabled:opacity-60";
  const btnGhost =
    "px-4 py-2.5 rounded-lg border border-slate-700 bg-slate-900/60 hover:bg-slate-800 text-slate-100";
  const btnDanger =
    "bg-red-600 hover:bg-red-500 text-white px-4 py-2.5 rounded-lg transition-colors disabled:opacity-60";

  const renderFormRegistro = () => (
    <div className={panel}>
      <p className="text-slate-300 mb-4">
        No tienes contrato activo. Selecciona un comisionista para registrarlo.
      </p>

      <div className="grid gap-5 md:grid-cols-2">
        <div>
          <label className={label}>Comisionista</label>
          <select
            className={input}
            value={selectedComisionistaId}
            onChange={(e) => setSelectedComisionistaId(e.target.value)}
          >
            <option value="">-- Selecciona --</option>
            {comisionistas.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre} {c.apellido} — {c.aniosExperiencia} año
                {c.aniosExperiencia !== 1 ? "s" : ""}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className={label}>Moneda</label>
          <select
            className={input}
            value={moneda}
            onChange={(e) => setMoneda(e.target.value)}
          >
            <option value="COP">COP</option>
            <option value="VES">VES</option>
            <option value="USD">USD</option>
            <option value="PEN">PEN</option>
          </select>
        </div>
      </div>

      <div className="mt-5">
        <label className={label}>Observaciones (opcional)</label>
        <textarea
          className={input}
          value={observaciones}
          onChange={(e) => setObservaciones(e.target.value)}
          rows={3}
          placeholder="Ej: Quiero trabajar con este comisionista."
        />
      </div>

      <div className="flex flex-col sm:flex-row sm:items-center gap-3 mt-5">
        <button
          type="button"
          className={btnGhost}
          onClick={() => setShowTerms(true)}
        >
          Ver términos
        </button>
        <label className="flex items-center gap-2 text-sm text-slate-300">
          <input
            type="checkbox"
            checked={acceptedTerms}
            onChange={(e) => setAcceptedTerms(e.target.checked)}
          />
          Acepto los términos
        </label>
      </div>

      <div className="mt-6">
        <button
          onClick={onRegistrarContrato}
          disabled={!selectedComisionistaId || !acceptedTerms || contratoSaving}
          className={btnPrimary}
        >
          {contratoSaving ? "Registrando..." : "Registrar contrato"}
        </button>
      </div>
    </div>
  );

  return (
    <div className={shell}>
      <div className={wrap}>
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Contrato
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <div className="max-w-5xl">
          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
        </div>

        {contratoLoading ? (
          <p className="text-slate-300 mt-4">Cargando contrato...</p>
        ) : contrato && contrato.estado === "ACTIVO" ? (
          <div className={`${panel} space-y-5`}>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {[
                {
                  label: "Comisionista:",
                  value: contrato.comisionistaNombreCompleto || "—",
                },
                {
                  label: "Inversionista:",
                  value:
                    (contrato.inversionistaNombreCompleto || "—") +
                    (contrato.inversionistaDocumento
                      ? ` (${contrato.inversionistaDocumento})`
                      : ""),
                },
                { label: "Estado:", value: contrato.estado },
                { label: "Moneda:", value: contrato.moneda },
                {
                  label: "Porcentaje cobro:",
                  value: `${contrato.porcentajeCobroAplicado}%`,
                },
                {
                  label: "Fecha inicio:",
                  value: contrato.fechaInicio || "—",
                },
                ...(contrato.fechaFin
                  ? [{ label: "Fecha fin:", value: contrato.fechaFin }]
                  : []),
              ].map((it, i) => (
                <div
                  key={i}
                  className="rounded-xl border border-slate-800 bg-slate-900/60 p-4 ring-1 ring-white/5"
                >
                  <div className="text-[11px] uppercase tracking-wide text-slate-400">
                    {it.label}
                  </div>
                  <div className="mt-1.5 text-slate-100 font-medium truncate">
                    {it.value}
                  </div>
                </div>
              ))}
            </div>

            <div className="pt-2">
              <button
                onClick={onCancelarContrato}
                disabled={contratoSaving}
                className={btnDanger}
              >
                {contratoSaving ? "Cancelando..." : "Cancelar contrato"}
              </button>
            </div>
          </div>
        ) : (
          renderFormRegistro()
        )}

        <Modal
          open={showTerms}
          title="Términos y Condiciones del Contrato"
          onClose={() => setShowTerms(false)}
          footer={
            <div className="flex justify-end gap-2">
              <button className={btnGhost} onClick={() => setShowTerms(false)}>
                Cerrar
              </button>
              <button
                className={btnPrimary}
                onClick={() => {
                  setAcceptedTerms(true);
                  setShowTerms(false);
                }}
              >
                Aceptar
              </button>
            </div>
          }
        >
          <pre className="whitespace-pre-wrap text-sm leading-6 text-slate-200">
            {buildTerminos()}
          </pre>
        </Modal>
      </div>
    </div>
  );
}
