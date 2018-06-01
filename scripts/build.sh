set -e
cd Latex
for i in *.tex; do 
    pdflatex -interaction=nonstopmode -halt-on-error $i
    pdflatex -interaction=nonstopmode -halt-on-error $i
done
