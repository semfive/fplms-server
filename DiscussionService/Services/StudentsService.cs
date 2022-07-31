using AutoMapper;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;

namespace DiscussionService.Services
{
    public class StudentsService : IStudentsService
    {
        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;
        public StudentsService(IMapper mapper, IRepositoryWrapper repositoryWrapper) { }

        public async Task<List<GetStudentDto>> GetAllStudents()
        {
            try
            {
                var students = await _repositoryWrapper.StudentRepository.GetAllStudentsAsync();
                var result = _mapper.Map<List<GetStudentDto>>(students);
                return result;
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public async Task<GetStudentDto> GetStudentById(Guid studentId)
        {
            try
            {
                var student = await _repositoryWrapper.StudentRepository.GetStudentByIdAsync(studentId);
                var result = _mapper.Map<GetStudentDto>(student);
                return result;
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public async Task<Guid> CreateStudent(CreateStudentDto createStudentDto)
        {
            try
            {
                Student student = _mapper.Map<Student>(createStudentDto);
                var studentExists = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(student.Email);
                if (studentExists != null)
                {
                    return studentExists.Id;
                }
                student.Id = Guid.NewGuid();
                _repositoryWrapper.StudentRepository.Create(student);
                await _repositoryWrapper.SaveAsync();
                return student.Id;
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public async Task<List<GetQuestionDto>> GetStudentQuestions(string userEmail)
        {
            try
            {
                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var questions = await _repositoryWrapper.QuestionRepository.GetQuestionsByStudentId(student.Id);

                if (!student.Id.Equals(questions.First().StudentId))
                {
                    throw new Exception("Only the author of the questions can get their questions");
                }

                var result = _mapper.Map<List<GetQuestionDto>>(questions);
                return result;
            }
            catch (Exception ex)
            {
                throw;
            }
        }


        public async Task<List<GetAnswerDto>> GetStudentAnswers(string userEmail)
        {
            try
            {
                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var answers = await _repositoryWrapper.AnswerRepository.GetAnswersByStudentId(student.Id);
                if (!student.Id.Equals(answers.First().StudentId))
                {
                    throw new Exception("Only the author of the answers can get their answers");
                }
                var result = _mapper.Map<List<GetAnswerDto>>(answers);
                return result;
            }
            catch (Exception ex)
            {
                throw;
            }
        }
    }
}