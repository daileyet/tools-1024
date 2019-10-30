# How to use

## Precondition

Installed JRE 1.8+

## Help

```shell
## in command line type
java -jar tools-1024-1.0.jar --HELP
## or run bat
help.bat
```



## PDF Tool

```shell
java -jar tools-1024-1.0.jar --PDF
## or run bat
pdf.bat
```

### HELP

```shell
## in command line type
pdf.bat -H
```

### Merge PDF

```shell
## in command line type
## pdf.bat -MERGE <pdf1 path> [<pdf2 path>...] -O <merged output pdf path>
## recommend absolute pdf file path
pdf.bat -MERGE 1.pdf 2.pdf 3.pdf -O target.pdf
```

```shell
## in command line type
## pdf.bat -MERGE <pdfs flder> -O <merged output pdf path> [-S [ASC|DESC]]
## merger all pdf file in the folder and sort by file name
pdf.bat -MERGE folder -O target.pdf -S DESC
## default ascending order
pdf.bat -MERGE folder -O target.pdf -S
```



### Watermark

```
## syntax
pdf.bat -MARK <pdf path> -T <mark text> 
[-F <font name:TIMES_BOLD>]
[-S <font size:50>]
[-R <font color:#FF0000>]
[-P <text rotate and position:45 150 150>]
[-O <marked output pdf path>]

Support font:
1. TIMES_ROMAN
2. TIMES_BOLD
3. TIMES_ITALIC
4. TIMES_BOLD_ITALIC
5. HELVETICA
6. HELVETICA_BOLD
7. HELVETICA_OBLIQUE
8. HELVETICA_BOLD_OBLIQUE
9. COURIER
10. COURIER_BOLD
11. COURIER_OBLIQUE
12. COURIER_BOLD_OBLIQUE
13. SYMBOL
14. ZAPF_DINGBATS
```

```shell
## in command line type
## use default font size, color, family, rotate, position for watermark text and direct apply input pdf
pdf.bat -MARK 1.pdf -T Openthinks 1024

## apply the watermark and save as new file
pdf.bat -MARK 1.pdf -T Openthinks 1024 -O marked.pdf

## apply the watermark for rotate angle 0(not rotate), translation x 200, y 100
pdf.bat -MARK 1.pdf -T Openthinks 1024 -P 0 200 100

## apply the watermark for given font size, name and translation
pdf.bat -MARK 1.pdf -T Openthinks 1024 -F HELVETICA_OBLIQUE -S 80 -P 0 200 100 -O marked.pdf
```



## Image Tool

```shell
java -jar tools-1024-1.0.jar --IMG
## or run bat
img.bat
```

### Crop

```shell
## in command line type
img.bat -I input.png -O output.jpg -CROP 0 0 32 32
```



### Convert format

```shell
## in command line type
img.bat -I input.png -O output.jpg -FORMAT JPG
```



### Mirror 

```shell
## in command line type
## used default mirror setting
img.bat -I input.png -O output.jpg -MIRROR
## used special mirror setting
img.bat -I input.png -O output.jpg -MIRROR -opacity 0.5 -centreY 0.5 -distance 1 -angle 0 -rotation 0 -gap 0
```

