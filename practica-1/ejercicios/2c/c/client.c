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
    //TOMA EL NUMERO DE PUERTO DE LOS ARGUMENTOS
    portno = atoi(argv[2]);

    //CREA EL FILE DESCRIPTOR DEL SOCKET PARA LA CONEXION
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    //AF_INET - FAMILIA DEL PROTOCOLO - IPV4 PROTOCOLS INTERNET
    //SOCK_STREAM - TIPO DE SOCKET

    if (sockfd < 0)
        error("ERROR opening socket");

    //TOMA LA DIRECCION DEL SERVER DE LOS ARGUMENTOS
    server = gethostbyname(argv[1]);
    if (server == NULL)
    {
        fprintf(stderr, "ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;

    //COPIA LA DIRECCION IP Y EL PUERTO DEL SERVIDOR A LA ESTRUCTURA DEL SOCKET
    bcopy((char *)server->h_addr,
          (char *)&serv_addr.sin_addr.s_addr,
          server->h_length);
    serv_addr.sin_port = htons(portno);

    //DESCRIPTOR - DIRECCION - TAMAÑO DIRECCION
    if (connect(sockfd, &serv_addr, sizeof(serv_addr)) < 0)
        error("ERROR connecting");
    bzero(buffer, bufferSize);

    /* Cargo el buffer en todas sus posiciones con el carácter 'a'.
    En la última posición indico el fin del string con '\0'.
    */

    for(int i = 0; i < bufferSize - 1; i++)
        buffer[i] = 'a';
    buffer[bufferSize - 1] = '\0';

    // Se calcula y envía el tamaño del mensaje en los primeros 4 bytes
    int converted = htonl(bufferSize);
    n = write(sockfd, &converted, sizeof(converted));
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

    //Se envía el mensaje
    n = write(sockfd, buffer, bufferSize);
    if (n < 0) error("ERROR writing to socket");
    bzero(buffer, bufferSize);

    //ESPERA RECIBIR UNA RESPUESTA
    n = read(sockfd, buffer, bufferSize - 1);
    if (n < 0)
        error("ERROR reading from socket");
    printf("%s\n", buffer);
    return 0;
}
