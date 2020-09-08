/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <math.h>
#include <openssl/md5.h>

void error(char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char *argv[])
{
    int sockfd, newsockfd, portno, clilen;
    unsigned int msgSize;
    unsigned char checksumRecibido[MD5_DIGEST_LENGTH];
    unsigned char checksumCalculado[MD5_DIGEST_LENGTH];
    struct sockaddr_in serv_addr, cli_addr;
    long int n, despla;
    if (argc < 2)
    {
        fprintf(stderr, "ERROR, no port provided\n");
        exit(1);
    }
    //CREA EL FILE DESCRIPTOR DEL SOCKET PARA LA CONEXION
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    //AF_INET - FAMILIA DEL PROTOCOLO - IPV4 PROTOCOLS INTERNET
    //SOCK_STREAM - TIPO DE SOCKET

    if (sockfd < 0)
        error("ERROR opening socket");
    bzero((char *) &serv_addr, sizeof(serv_addr));
    //ASIGNA EL PUERTO PASADO POR ARGUMENTO
    //ASIGNA LA IP EN DONDE ESCUCHA (SU PROPIA IP)
    portno = atoi(argv[1]);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);

    //VINCULA EL FILE DESCRIPTOR CON LA DIRECCION Y EL PUERTO
    if (bind(sockfd, (struct sockaddr *) &serv_addr,
             sizeof(serv_addr)) < 0)
        error("ERROR on binding");

    //SETEA LA CANTIDAD QUE PUEDEN ESPERAR MIENTRAS SE MANEJA UNA CONEXION
    listen(sockfd, 5);

    // SE BLOQUEA A ESPERAR UNA CONEXION
    clilen = sizeof(cli_addr);
    newsockfd = accept(sockfd,
                       (struct sockaddr *) &cli_addr,
                       &clilen);

    //DEVUELVE UN NUEVO DESCRIPTOR POR EL CUAL SE VAN A REALIZAR LAS COMUNICACIONES
    if (newsockfd < 0)
        error("ERROR on accept");

    //LEE EL MENSAJE DEL CLIENTE

    //Leo el tamaño del mensaje
    n = read(newsockfd, &msgSize, 4);
    if (n < 0) error("ERROR reading from socket");
    int bufferSize = ntohl(msgSize);
    unsigned char buffer[bufferSize];
    bzero(buffer, bufferSize);

    //leo el checksum del mensaje
    n = read(newsockfd, checksumRecibido, MD5_DIGEST_LENGTH);
    if (n < 0) error("ERROR reading from socket");
    printf("Tamaño del mensaje: %d\n", bufferSize);

    //leo el mensaje recibido
    despla = 0;
    while(despla < bufferSize)
    {
        n = read(newsockfd, buffer + despla, bufferSize - despla);
        if (n < 0) error("ERROR reading from socket");
        despla += n;
        printf("Tamaño de desplazamiento: %ld\n", despla);
    }

    printf("\n");

    // Calculo el checksum del mensaje recibido.
    unsigned char output1[MD5_DIGEST_LENGTH];

    MD5_CTX md5;
    MD5_Init(&md5);
    MD5_Update(&md5, buffer, bufferSize);
    MD5_Final(output1, &md5);
    
    printf("El checksum recibido es: ");
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++)
        printf("%02x", checksumRecibido[i]);
    printf("\n");

    printf("El checksum del mensaje es: ");
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++)
        printf("%02x", output1[i]);
    printf("\n");    

    //RESPONDE AL CLIENTE
    n = write(newsockfd, "I got your message", 18);
    if (n < 0) error("ERROR writing to socket");
    return 0;
}