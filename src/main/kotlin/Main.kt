import java.util.PriorityQueue

var count = 100000
var tempoGlobal: Double = 2.0
val filaCapacidade = 5
val servidores = 2
var filaCount = 0
var perdaCount = 0
val minChegada = 2
val maxChegada = 5
val minSaida = 3
val maxSaida = 5
val tempoNClientesFila = MutableList(filaCapacidade + 1) { 0.0 }
val escalonador = PriorityQueue<Evento>()

fun main(args: Array<String>) {

    var eventoAnteriorTempoGlobal = tempoGlobal
    tempoNClientesFila[0] += tempoGlobal
    escalonador.add(Evento(TIPO_EVENTO.CHEGADA, tempoDemandado(TIPO_EVENTO.CHEGADA)))

    while (count > 0) {
        val evento = proximoEvento()

        var tempoDeDiferenca = evento.tempo - eventoAnteriorTempoGlobal
        eventoAnteriorTempoGlobal = evento.tempo
        tempoNClientesFila[filaCount] += tempoDeDiferenca

        if (evento.tipo == TIPO_EVENTO.CHEGADA) {
            chegada(evento)
        } else if (evento.tipo == TIPO_EVENTO.SAIDA) {
            saida(evento)
        }
        count--
    }

    println("tempo global: $tempoGlobal")
    println("perdas: $perdaCount")
    tempoNClientesFila.forEachIndexed { index, tempo ->
        println()
        println("pessoas na fila: $index ")
        println("tempo: $tempo")
        println("porcentagem de tempo: ${(tempo/tempoGlobal) * 100}%")
    }
}

fun chegada(evento: Evento) {
    tempoGlobal = evento.tempo

    if (filaCount < filaCapacidade) {
        filaCount++
        if (filaCount <= servidores) {
            agendaEvento(Evento(TIPO_EVENTO.SAIDA, tempoGlobal + tempoDemandado(TIPO_EVENTO.SAIDA)))
        }
    } else {
        perdaCount++
    }
    agendaEvento(Evento(TIPO_EVENTO.CHEGADA, tempoGlobal + tempoDemandado(TIPO_EVENTO.CHEGADA)))
}

fun saida(evento: Evento) {
    tempoGlobal = evento.tempo
    filaCount--
    if (filaCount >= servidores) {
        agendaEvento(Evento(TIPO_EVENTO.SAIDA, tempoGlobal + tempoDemandado(TIPO_EVENTO.SAIDA)))
    }
}

fun proximoEvento(): Evento = escalonador.poll()

fun agendaEvento(evento: Evento) {
    escalonador.add(evento)
}

enum class TIPO_EVENTO() {
    CHEGADA, SAIDA
}

object GeradorDeNumero {
    var anterior = 0.0
    val a = 1987571865
    val c = 174153
    val m = Math.pow(2.0, 24.0)

    fun aleatorio(): Double {

        anterior = (a * anterior + c) % m
        return anterior / m
    }
}

fun tempoDemandado(tipoEvento: TIPO_EVENTO): Double {
    var a = 0
    var b = 0

    if (tipoEvento == TIPO_EVENTO.CHEGADA) {
        a = minChegada
        b = maxChegada
    } else if (tipoEvento == TIPO_EVENTO.SAIDA) {
        a = minSaida
        b = maxSaida
    }
    val test = GeradorDeNumero.aleatorio()
    val resultado = a + ((b - a) * test)
    return resultado
}
data class Evento(val tipo: TIPO_EVENTO, val tempo: Double) : Comparable<Evento> {

    override fun compareTo(other: Evento): Int {
        return this.tempo.compareTo(other.tempo)
    }

}