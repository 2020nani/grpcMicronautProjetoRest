package br.com.hernani.recebefuncionarioviagrpc

import br.com.hernani.Cargo
import br.com.hernani.ErrorDetails
import br.com.hernani.FuncionarioRequest
import br.com.hernani.FuncionarioServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import org.slf4j.LoggerFactory
import javax.inject.Inject

@Controller
class cadastraFuncionarioController(
    @Inject val grpcClient: FuncionarioServiceGrpc.FuncionarioServiceBlockingStub
) {
    //instancia logger
    val logger = LoggerFactory.getLogger(cadastraFuncionarioController::class.java)

    @Get("/api/funcionario")
    fun cadastraFuncionario(
        @QueryValue nome: String,
        @QueryValue cpf: String,
        @QueryValue idade: Int,
        @QueryValue cargo: Cargo
    ): Response {
        logger.info("Iniciando requisicao")
        val request = FuncionarioRequest.newBuilder()
            .setNome(nome)
            .setCpf(cpf)
            .setIdade(idade)
            .setCargo(cargo)
            .build()

        try {
            val response = grpcClient.cadastraFuncionario(request);
            logger.info("requisicao finalizada com sucesso, id funcionario: $response")

            val funcionarioResponse = Response("Funcionario cadastrado com sucesso", response.id)
            return funcionarioResponse
        } catch (e: StatusRuntimeException) {
            logger.info("Falha na requisicao do grpcClient")
            val description = e.status.description
            val statusCode = e.status.code
            if (statusCode == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }
            //extraindo error details
            if (statusCode == Status.Code.PERMISSION_DENIED) {
                val statusProto = StatusProto.fromThrowable(e)
                if (statusProto == null) {
                    throw HttpStatusException(HttpStatus.FORBIDDEN, description)
                }
                val anyDetails = statusProto.detailsList.get(0)
                val errorDetails = anyDetails.unpack(ErrorDetails::class.java)
                throw  HttpStatusException(HttpStatus.FORBIDDEN,"${errorDetails.code}: ${errorDetails.message}")
            }
            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)

        }


    }
}

data class Response(
    val mensagem: String,
    val id: Long
)

