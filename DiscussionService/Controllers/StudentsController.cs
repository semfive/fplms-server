using System.Data;
using AutoMapper;
using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/students")]
    public class StudentsController : ControllerBase
    {

        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;

        public StudentsController(IMapper mapper, IRepositoryWrapper repositoryWrapper)
        {
            _mapper = mapper;
            _repositoryWrapper = repositoryWrapper;
        }

        [HttpGet]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetAllStudents()
        {
            try
            {
                var students = await _repositoryWrapper.StudentRepository.GetAllStudentsAsync();
                var result = _mapper.Map<List<GetStudentDto>>(students);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{studentId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetStudentById([FromRoute] Guid studentId)
        {
            try
            {
                var student = await _repositoryWrapper.StudentRepository.GetStudentByIdAsync(studentId);
                var result = _mapper.Map<GetStudentDto>(student);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateStudent(CreateStudentDto createStudentDto)
        {
            try
            {
                Student student = _mapper.Map<Student>(createStudentDto);
                var studentExists = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(student.Email);
                if (studentExists != null)
                {
                    return Ok();
                }
                student.Id = Guid.NewGuid();
                _repositoryWrapper.StudentRepository.Create(student);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (DataException)
            {
                Console.WriteLine("Unable to save data");
                return StatusCode(500, "Internal server error");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
                return StatusCode(500, "Internal server error");
            }

        }

        [HttpGet("{studentId}/questions")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetStudentQuestions([FromRoute] Guid studentId)
        {
            try
            {
                var questions = await _repositoryWrapper.QuestionRepository.GetQuestionsByStudentId(studentId);
                var result = _mapper.Map<List<GetQuestionDto>>(questions);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{studentId}/answers")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetStudentAnswers([FromRoute] Guid studentId)
        {
            try
            {
                var answers = await _repositoryWrapper.AnswerRepository.GetAnswersByStudentId(studentId);
                var result = _mapper.Map<List<GetAnswerDto>>(answers);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

    }
}