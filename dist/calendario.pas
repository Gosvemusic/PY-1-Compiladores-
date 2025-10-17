program calendario;
uses crt, dos;

const borde = #205#205#205#205#205#205#205#205#205;
const months       : array[0..11] of integer = (31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
const year_regular : array[0..11] of integer = ( 0,  3,  3,  6,  1,  4,  6,  2,  5,  0,  3,  5);
const year_bisiesto : array[0..11] of integer = ( 0,  3,  4,  0,  2,  5,  0,  3,  6,  1,  4,  6);
const nombres      : array[0..11] of string  = (
    '  Enero   ', ' Febrero  ', '  Marzo   ', '  Abril   ',
    '   Mayo   ', '  Junio   ', '  Julio   ', '  Agosto  ',
    'Septiembre', ' Octubre  ', 'Noviembre ', 'Diciembre ');

var meses : array[0..11] of integer;
var tecla : integer;
var i : integer;
var dia : integer;
var modulo : integer;
var day : word;
var year : word;
var mes : word;
var dayofweek : word;

begin
{inicio del programa}
    for i:=0 to 11 do
        begin
            meses[i] := months[i];
        end;
    getdate (year, mes , day, dayofweek);
    dec (mes);
    repeat
        begin
            if (((year mod 4) = 0) and ((year mod 100) <> 0)) or ((year mod 400) = 0) then
                begin
                    meses[1] := 29;
                    modulo := year_bisiesto[mes];
                end
            else
                begin
                    meses[1] := 28;
                    modulo := year_regular[mes];
                end;
            dia := 1 - ((year - 1) mod 7 + ((year - 1) div 4 - (3 * ((year - 1) div 100 + 1)) div 4) mod 7 + modulo + 1) mod 7;
            clrscr;
            writeln ('          '#17, nombres[mes], #16'                       '#30, year, #31);
            writeln (#201, borde, #203, borde, #203, borde, #203, borde, #203, borde, #203, borde, #203, borde, #187);
            write   (#186' Domingo '#186'  Lunes  '#186' Martes  '#186'Mi'#130'rcoles'#186' Jueves  '#186' Viernes ');
            writeln (#186' S'#160'bado  '#186);
            writeln (#204, borde, #206, borde, #206, borde, #206, borde, #206, borde, #206, borde, #206, borde, #185);
            while dia <= meses[mes] do
                begin
                    for i:=0 to 6 do
                        begin
                            if (dia<1) or (dia>meses[mes]) then
                                begin
                                    write (#186'         ')
                                end	
                            else
                                begin
                                    write (#186'   ', dia:2, '    ');
                                end;	
                            inc (dia);
                        end;
                    writeln (#186);
                    if dia<=meses[mes] then
                        begin
                            writeln (#204, borde, #206, borde, #206, borde, #206, borde, #206, borde, #206, borde, #206, borde, #185)
                        end	
                    else
                        begin
                            writeln (#200, borde, #202, borde, #202, borde, #202, borde, #202, borde, #202, borde, #202, borde, #188);
                        end; 
                end;
            writeln ('Presione '#27' y '#26' para cambiar de mes.');
            writeln ('Presione '#24' y '#25' para cambiar de a'#164'o.');
            writeln ('Presione ESC para salir.');
            repeat
                begin
                    tecla := ord (readkey);
                end;
            until (tecla=0) or (tecla=27);
            if tecla = 0 then
                begin
                    case ord (readkey) of
                        72: inc (year);
                        80: dec (year);
                        77:
                        if mes<11 then
                            begin
                                inc (mes);
                            end	
                        else
                            begin
                                inc (year);
                                mes := 0;
                            end;
                        75:
                            if mes <> 0 then
                                begin
                                    dec (mes);
                                end
                            else
                                begin
                                    dec (year);
                                    mes := 11;
                                end;
               end;
          end;	
        end;
    until tecla = 27;
    //fin del programa
end. 