#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <math.h>
#include <openssl/md5.h>
#include <stdlib.h>

void error(char *msg)
{
    perror(msg);
    exit(0);
}

int main(int argc, char *argv[])
{
    int bufferSize = pow(10, 6);
    int sockfd, portno, n;

    struct sockaddr_in serv_addr;
    struct hostent *server;

    unsigned char buffer[bufferSize];
    unsigned char checksum[MD5_DIGEST_LENGTH];

    if (argc < 3)
    {
        fprintf(stderr, "usage %s hostname port\n", argv[0]);
        exit(0);
    }
    // Toma el numero de puerto de los argumentos
    portno = atoi(argv[2]);

    // Crea el file descriptor del socket para la conexion
    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0)
        error("ERROR opening socket");

    // Toma la direccion del server de los argumentos
    server = gethostbyname(argv[1]);
    if (server == NULL)
    {
        fprintf(stderr, "ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;

    // Copia la direccion ip y el puerto del servidor a la estructura del socket
    bcopy((char *)server->h_addr,
          (char *)&serv_addr.sin_addr.s_addr,
          server->h_length);
    serv_addr.sin_port = htons(portno);

    // Descriptor - direccion - tamaño direccion
    if (connect(sockfd, &serv_addr, sizeof(serv_addr)) < 0)
        error("ERROR connecting");
    bzero(buffer, bufferSize);

    /* Cargo el buffer en todas sus posiciones con el carácter 'a'.
    En la última posición indico el fin del string con '\0'. */
    for(int i = 0; i < bufferSize - 1; i++)
        buffer[i] = 'a';
    buffer[bufferSize - 1] = '\0';

    // Se convierte el tamaño al órden de bytes del sistema
    int sizeConverted = htonl(bufferSize);

    // Se envía el tamaño del mensaje
    n = write(sockfd, &sizeConverted, sizeof(sizeConverted));
    if (n < 0) error("ERROR writing to socket");

    // Se calcula el checksum
    MD5(buffer, bufferSize, checksum);

    printf("El checksum es: ");
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++)
        printf("%02x", checksum[i]);
    printf("\n");

    // Se envía el checksum
    n = write(sockfd, checksum, MD5_DIGEST_LENGTH);
    if (n < 0) error("ERROR writing to socket");

    // Se envía el mensaje
    n = write(sockfd, buffer, bufferSize);
    if (n < 0) error("ERROR writing to socket");
    bzero(buffer, bufferSize);

    // Espera recibir una respuesta del servidor
    n = read(sockfd, buffer, bufferSize - 1);
    if (n < 0) error("ERROR reading from socket");

    printf("%s\n", buffer);

    return 0;
}
