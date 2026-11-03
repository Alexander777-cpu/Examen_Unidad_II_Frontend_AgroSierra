package pe.upeu.agrosierra.domain.model

object ReglasNegocio {
    fun validarRegistroLabor(lote: Lote, tipo: TipoLabor, cantidad: Double): Result<Unit> {
        if (lote.campaniaCerrada) {
            return Result.failure(IllegalArgumentException("RN-01: No se pueden registrar labores en un lote con campaña cerrada"))
        }
        if (tipo == TipoLabor.FUMIGACION) {
            val dosisMaximaPermitida = lote.dosisMaximaPorHectarea * lote.hectareas
            if (cantidad > dosisMaximaPermitida) {
                return Result.failure(IllegalArgumentException("RN-04: La dosis total excede el máximo permitido ($dosisMaximaPermitida)"))
            }
        }
        return Result.success(Unit)
    }

    fun validarCierreCampania(labores: List<Labor>): Result<Unit> {
        val tieneCosecha = labores.any { it.tipo == TipoLabor.COSECHA }
        if (!tieneCosecha) {
            return Result.failure(IllegalArgumentException("RN-03: No se puede cerrar la campaña sin al menos una labor de COSECHA"))
        }
        return Result.success(Unit)
    }
}
